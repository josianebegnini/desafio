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
	private AssociadoExternoService associadoExternoService;

    private static final Logger logger = LogManager.getLogger(VotacaoService.class);
	
    public VotacaoService(VotacaoRepository votacaoRepo, SessaoService sessaoService, AssociadoService associadoService, AssociadoExternoService associadoExternoService) {
        this.votacaoRepo = votacaoRepo;
        this.sessaoService = sessaoService;
        this.associadoService = associadoService;
        this.associadoExternoService = associadoExternoService;
    }
    
    public boolean validaAssociadoJaVotou(Votacao votacao) throws Exception {
    	if(votacao.getAssociado()!=null && votacao.getAssociado().getCpf()!=null) {
    		List<Votacao> associado = votacaoRepo.findByAssociadoCpf(votacao.getAssociado().getCpf());
			if(associado!=null && !associado.isEmpty()) {
				return true;
			}
			logger.info("Associado já votou na sessão");
			throw new VotacaoException("Associado já votou na sessão");
    	}else {
    		throw new VotacaoException("CPF inválido");
    	}
    }
    
    public void votar(Votacao votacao) throws Exception {
    	try {
    		
    		validaVotacaoEncerrada(votacao);
    		
			Optional<Sessao> sessao = buscaSessao(votacao);
    		chamaServicoExternoUsuarioPossuiPermissaoVotar(votacao);
    			
    			if(!tempoDaSessaoExpirou(sessao)) {
    				Associado associado = validaAssociadoEhCadastrado(votacao);
    				validaAssociadoJaVotou(votacao);
    				
    			
    				
    				votacao.setAssociado(associado);
    				votacaoRepo.save(votacao);
    			}
    	} catch (DataAccessException e) {
    		throw new ServiceException("Erro ao votar: " + e.getMessage(), e);
    	}
    }

	private Associado validaAssociadoEhCadastrado(Votacao votacao) {
		Associado associado = associadoService.buscarPorCpf(votacao.getAssociado().getCpf());
		if(associado==null) {
			logger.info("Associado não possui permissão de voto, pois não esta cadastrado");
			throw new VotacaoException("Associado não possui permissão de voto, pois não esta cadastrado");
		}
		return associado;
	}

	private void chamaServicoExternoUsuarioPossuiPermissaoVotar(Votacao votacao) {
		try {
			boolean resultado = associadoExternoService.chamaServicoExternoAssociadoPossuiPermissaoParaVotar(votacao);
			if(!resultado) {
				logger.info("Associado nao possui permissão para votar");
				throw new VotacaoException("Associado nao possui permissão para votar");
			}
		} catch (Exception e) {
			logger.info("Não foi possível validar associado no serviço externo, serviço pode estar fora.");
			
		}
	}

	private void validaVotacaoEncerrada(Votacao votacao) {
		if(votacao.getSessao() != null && votacao.getSessao().isFechada()) {
			logger.info("A votação está encerrada.");
			throw new VotacaoException("A votação está encerrada.");
		}
	}

	private Optional<Sessao> buscaSessao(Votacao votacao) {
		Optional<Sessao> sessao = sessaoService.buscarPorId(votacao.getSessao().getIdSessao());
		if(sessao==null) {
			logger.error("Sessão não encontrada");
			throw new VotacaoException("Sessão não encontrada");
		}
		return sessao;
	}

	private boolean tempoDaSessaoExpirou(Optional<Sessao> sessao) {
		LocalDateTime dataVotacao = sessao.get().getDtSessao();
		long tempoLimiteMinutos = sessao.get().getDuracao();
		LocalDateTime agora = LocalDateTime.now();
		long diferencaMinutos = Duration.between(agora, dataVotacao).toMinutes();
		if (diferencaMinutos > tempoLimiteMinutos) {
			encerrarSessao(sessao.get());
			logger.info("Sessão expirou e foi encerrada");
			return true;
		}
		return false;
	}

	public void encerrarSessao(Sessao sessaoVotacao) {
		Optional<Sessao> sessao = sessaoService.buscarPorId(sessaoVotacao.getIdSessao());
		if(sessao==null) {
			logger.error("Sessão não encontrada");
			throw new VotacaoException("Sessão não encontrada");
		}

		sessao.get().setFechada(true);
		sessao.get().setDtEncerramento(LocalDateTime.now());
		sessaoService.atualizarSessao(sessao.get());
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
