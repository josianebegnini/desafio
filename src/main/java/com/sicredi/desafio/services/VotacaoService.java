package com.sicredi.desafio.services;

import java.text.Normalizer;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.service.spi.ServiceException;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.sicredi.desafio.exceptions.VotacaoException;
import com.sicredi.desafio.models.Associado;
import com.sicredi.desafio.models.ResultadoVotacao;
import com.sicredi.desafio.models.Sessao;
import com.sicredi.desafio.models.Votacao;
import com.sicredi.desafio.repositories.VotacaoRepository;

@Service
public class VotacaoService {
	private VotacaoRepository votacaoRepo;
	private SessaoService sessaoService;
	private AssociadoService associadoService;
	private MessageProducer producer;

    private static final Logger logger = LogManager.getLogger(VotacaoService.class);
	private final String SERVICO_EXETERNO_URL = "https://user-info.herokuapp.com/users/";
	
    public VotacaoService(VotacaoRepository votacaoRepo, SessaoService sessaoService, AssociadoService associadoService, MessageProducer producer) {
        this.votacaoRepo = votacaoRepo;
        this.sessaoService = sessaoService;
        this.associadoService = associadoService;
        this.producer = producer;
    }
    
    public boolean associadoPossuiPermissaoParaVotar(Votacao votacao) throws Exception {
    	if(votacao.getAssociado()!=null && votacao.getAssociado().getCpf()!=null) {
    		String cpfNormalizado = Normalizer.normalize(votacao.getAssociado().getCpf(), Normalizer.Form.NFD)
    				.replaceAll("\\p{M}", "");
    		String url = SERVICO_EXETERNO_URL + cpfNormalizado;
    		try {
    			RestTemplate restTemplate = new RestTemplate();
    			ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

    			if(response.getBody().equals("ABLE_TO_VOTE")) {
    				return true;
    			}
    			return false;
    		} catch (HttpStatusCodeException ex) {
    			 if (ex.getStatusCode()== HttpStatus.NOT_FOUND)
    				 logger.info("Serviço externo retornou CPF inválido: " + ex);
    	             throw new VotacaoException("CPF Inválido!");
	    	} catch (Exception e) {
	    		logger.error("Erro ao chamar o serviço de validação de associado: " + e);
	    		throw new Exception("Erro ao chamar o serviço de validação de associado: " + e);
	    	}
    	}else {
        	logger.error("CPF não informado na votação");
    		throw new VotacaoException("CPF inválido");
    	}
    }
    public boolean associadoJaVotou(Votacao votacao) throws Exception {
    	if(votacao.getAssociado()!=null && votacao.getAssociado().getCpf()!=null) {
    		List<Votacao> associado = votacaoRepo.findByAssociadoCpf(votacao.getAssociado().getCpf());
			if(associado!=null && !associado.isEmpty()) {
				return true;
			}
			return false;
    	}else {
    		throw new VotacaoException("CPF inválido");
    	}
    }
    
    public void votar(Votacao votacao) throws Exception {
    	try {
    		if(votacao.getSessao() != null && !votacao.getSessao().isFechada()) {
    			Optional<Sessao> sessao = sessaoService.buscarPorId(votacao.getSessao().getIdSessao());
    			if(sessao==null) {
    				logger.error("Sessão não encontrada");
    				throw new VotacaoException("Sessão não encontrada");
    			}
    			try {
    				if(!associadoPossuiPermissaoParaVotar(votacao)) {
    					logger.info("Associado nao possui permissão para votar");
    					throw new VotacaoException("Associado nao possui permissão para votar");
    				}
    			} catch (Exception e) {
    				logger.info("Serviço de validação de permissão de voto externo retornou erro. "
    						+ "Alterado modo de validação pra encontrar se associado é cadastrado no serviço interno.");
    				
    			}
    			
    			if(!tempoDaSessaoExpirou(sessao)) {
    				Associado associado = associadoService.buscarPorCpf(votacao.getAssociado().getCpf());
    				if(associado==null) {
    					logger.info("Associado não possui permissão de voto, pois não esta cadastrado");
    					throw new VotacaoException("Associado não possui permissão de voto, pois não esta cadastrado");
    				}
    				if(associadoJaVotou(votacao)) {
    					logger.info("Associado já votou na sessão");
    					throw new VotacaoException("Associado já votou na sessão");
    				}
    				
    				votacao.setAssociado(associado);
    				votacaoRepo.save(votacao);
    			}
    		}else {
    			logger.info("A votação está encerrada.");
    			throw new VotacaoException("A votação está encerrada.");
    		}
    	} catch (DataAccessException e) {
    		throw new ServiceException("Erro ao votar: " + e.getMessage(), e);
    	}
    }

	private boolean tempoDaSessaoExpirou(Optional<Sessao> sessao) {
		LocalDateTime dataVotacao = sessao.get().getDtSessao();
		long tempoLimiteMinutos = sessao.get().getDuracao();
		LocalDateTime agora = LocalDateTime.now();
		long diferencaMinutos = Duration.between(agora, dataVotacao).toMinutes();
		if (diferencaMinutos > tempoLimiteMinutos) {
			encerrarSessao(sessao);
			logger.info("Sessão expirou e foi encerrada");
			return true;
		}
		return false;
	}

	private void encerrarSessao(Optional<Sessao> sessao) {
		sessao.get().setFechada(true);
		sessao.get().setDtEncerramento(LocalDateTime.now());
		sessaoService.atualizarSessao(sessao.get());
	}
    
	public ResultadoVotacao contabilizar(Sessao s) throws Exception {
		ResultadoVotacao resultado = new ResultadoVotacao();
		try {
			Optional<Sessao> sessao = encerraSessaoParaContagemDeVotos(s);
			
			List<Votacao> votos = votacaoRepo.findBySessaoIdSessao(s.getIdSessao());
			int contagemVotosSim = 0;
			int contagemVotosNao = 0;

			for (Votacao votacao : votos) {
				String votoNormalizado = Normalizer.normalize(votacao.getVoto(), Normalizer.Form.NFD)
						.replaceAll("\\p{M}", "");
				if ("sim".equalsIgnoreCase(votacao.getVoto())) {
					contagemVotosSim++;
				}
				if ("nao".equalsIgnoreCase(votoNormalizado)) {
					contagemVotosNao++;
				}
			}
			
			resultado.setTotalNegativo(contagemVotosNao);
			resultado.setTotalPositivo(contagemVotosSim);
			resultado.setSessaoId(sessao.get().getIdSessao());
		
			enviaMensagemParaFila(resultado);
			
			return resultado;
		} catch (DataAccessException de) {
			logger.error("Erro ao contabilizar os votos: " + de.getMessage(), de);
			throw new ServiceException("Erro ao contabilizar os votos: " + de.getMessage(), de);
		}catch (Exception e) {
			logger.error("Erro ao contabilizar os votos: " + e.getMessage(), e);
		}
		return resultado;
	}

	private void enviaMensagemParaFila(ResultadoVotacao resultado) throws Exception {
		String mensagem = montaMensagem(resultado);
		try {
			producer.sendMessage(mensagem);
			
		} catch (Exception e) {
			logger.error("erro ao postar na fila", e);
		}
	}

	private Optional<Sessao> encerraSessaoParaContagemDeVotos(Sessao s) {
		Optional<Sessao> sessao = sessaoService.buscarPorId(s.getIdSessao());
		if(sessao==null) {
			logger.error("Sessão não encontrada");
			throw new VotacaoException("Sessão não encontrada");
		}
		encerrarSessao(sessao);
		logger.info("Sessão de votação encerrada para contagem de votos");
		return sessao;
	}
	private String montaMensagem(ResultadoVotacao resultado) {
		Gson gson = new Gson();
		String json = gson.toJson(resultado);
		logger.info("Mensagem JSON gerada: {}", json);
		return json;
	}
	
	public List<Votacao> listarVotacoes(){
		try {
			return votacaoRepo.findAll();
		} catch (DataAccessException e) {
			throw new ServiceException("Erro ao listar votacoes: " + e.getMessage(), e);
		}
    }
    public Optional<Votacao> buscarPorId(Long id){
    	try {
    		return votacaoRepo.findById(id);
    	} catch (DataAccessException e) {
    		throw new ServiceException("Erro ao buscar votacao por ID: " + e.getMessage(), e);
    	}
    }
    public void atualizarVotacao(Votacao votacao){
    	try {
    		votacaoRepo.save(votacao);
    	} catch (DataAccessException e) {
    		throw new ServiceException("Erro ao atualizar votacao: " + e.getMessage(), e);
    	}
    }
    public void removerVotacaoPorId(Long id){
    	try {
    		votacaoRepo.deleteById(id);
    	} catch (DataAccessException e) {
    		throw new ServiceException("Erro ao remover votacao: " + e.getMessage(), e);
    	}
    }
}
