package com.sicredi.desafio.services;

import java.text.Normalizer;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.hibernate.service.spi.ServiceException;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.sicredi.desafio.exceptions.VotacaoEncerradaException;
import com.sicredi.desafio.models.Pauta;
import com.sicredi.desafio.models.ResultadoVotacao;
import com.sicredi.desafio.models.Sessao;
import com.sicredi.desafio.models.Votacao;
import com.sicredi.desafio.repositories.VotacaoRepository;

@Service
public class VotacaoService {
	private VotacaoRepository votacaoRepo;
	private PautaService pautaService;
	private SessaoService sessaoService;
	
	
    public VotacaoService(VotacaoRepository votacaoRepo, PautaService pautaService, SessaoService sessaoService) {
        this.votacaoRepo = votacaoRepo;
        this.pautaService = pautaService;
        this.sessaoService = sessaoService;
    }
    
    public void abrirSessaoVotacao(Votacao votacao) {
    	try {
    		if(votacao.getSessao() != null) {
    			Sessao sessao =  votacao.getSessao();
    			sessao.setFechada(false);
    			if(sessao.getDuracao() <= 0) {
    				sessao.setDuracao(1);
    			}
    			sessao.setDuracao(votacao.getSessao().getDuracao());
    			sessao.setDtSessao(LocalDateTime.now());
    			sessaoService.atualizarSessao(sessao);
    		}
    		if(votacao.getPauta()!=null) {
    			Pauta pauta = votacao.getPauta();
    			pauta.setFechada(true);
    			pautaService.atualizarPauta(pauta);
    		}
    	} catch (DataAccessException e) {
    		throw new ServiceException("Erro ao abrir Sessao de votacao: " + e.getMessage(), e);
    	}
    }
    
    public void votar(Votacao votacao) {
    	try {
    		//buscar se o usuario possui permissao para votar servico externo

    		if(votacao.getSessao() != null && !votacao.getSessao().isFechada()) {
    			Sessao sessao =  votacao.getSessao();
    			LocalDateTime dataVotacao = sessao.getDtSessao();
    			long tempoLimiteMinutos = sessao.getDuracao();
    			LocalDateTime agora = LocalDateTime.now();
    			long diferencaMinutos = Duration.between(agora, dataVotacao).toMinutes();
    			if (diferencaMinutos > tempoLimiteMinutos) {
    				sessao.setFechada(true);
    				sessao.setDtEncerramento(agora);
    				sessaoService.atualizarSessao(sessao);
    				throw new VotacaoEncerradaException();
    			}else {
    				votacaoRepo.save(votacao);
    			}
    		}else {
    			throw new VotacaoEncerradaException();
    		}
    	} catch (DataAccessException e) {
    		throw new ServiceException("Erro ao votar: " + e.getMessage(), e);
    	}
    }
    
    public ResultadoVotacao contabilizar(Sessao sessao) {
    	try {
    	 List<Votacao> votos = votacaoRepo.findBySessaoIdSessao(sessao.getIdSessao());
    	 ResultadoVotacao resultado = new ResultadoVotacao();
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
    	 return resultado;
    	} catch (DataAccessException e) {
			throw new ServiceException("Erro ao buscar votos sim: " + e.getMessage(), e);
		}
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
