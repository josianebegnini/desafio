package com.sicredi.desafio.services;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.hibernate.service.spi.ServiceException;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

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
    				//avisa que a sessao de votacao foi encerrada
    			}else {
    				votacaoRepo.save(votacao);
    			}
    		}else {
    			//avisa que a sessao de votacao foi encerrada
    		}
    	} catch (DataAccessException e) {
    		throw new ServiceException("Erro ao votar: " + e.getMessage(), e);
    	}

    
    }
    
    public ResultadoVotacao contabilizar(Sessao s) {
    	try {
    		ResultadoVotacao resultado = new ResultadoVotacao();
    		Sessao sessao = sessaoService.buscarPorId(s.getIdSessao()).get();
    		if(sessao != null && sessao.isFechada()) {
    			//    		resultado.setTotalPositivo(obterVotosComSimPorSessao(sessao).size());
    			//    		resultado.setTotalNegativo(obterVotosComNaoPorSessao(sessao).size());
    		}
    		return resultado;
    	} catch (DataAccessException e) {
    		throw new ServiceException("Erro ao contabilizar votacao: " + e.getMessage(), e);
    	}
    }
    
//    public List<Votacao> obterVotosComSimPorSessao(Sessao sessao) {
////        return votacaoRepo.findBySessaoAndVotoSim(sessao);
//    }
//    
//    public List<Votacao> obterVotosComNaoPorSessao() {
//    
////    	return votacaoRepo.findBySessaoAndVotoSim(sessao);
//    }
    
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
