package com.sicredi.desafio.services;

import java.text.Normalizer;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.service.spi.ServiceException;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.sicredi.desafio.models.ResultadoVotacao;
import com.sicredi.desafio.models.Sessao;
import com.sicredi.desafio.models.Votacao;
import com.sicredi.desafio.repositories.VotacaoRepository;

@Service
public class ContabilizaService {
	private VotacaoRepository votacaoRepo;
	private RabbitMQService rabbitMQService;
	private VotacaoService votacaoService;

	private static final Logger logger = LogManager.getLogger(ContabilizaService.class);
	
	 public ContabilizaService(VotacaoRepository votacaoRepo, VotacaoService votacaoService, RabbitMQService rabbitMQService) {
	        this.votacaoRepo = votacaoRepo;
	        this.rabbitMQService = rabbitMQService;
	        this.votacaoService = votacaoService;
	    }

	public String contabilizar(Sessao sessao) throws Exception {

		String retorno = null;
		try {
			encerraSessaoParaContagemDeVotos(sessao);

			ResultadoVotacao resultado = contabilizaVotosDaSessao(sessao);
			
			retorno = postaNaFilaResultadoVotacao(resultado);

			return retorno;
		} catch (DataAccessException de) {
			logger.error("Erro ao contabilizar os votos: " + de.getMessage(), de);
			throw new ServiceException("Erro ao contabilizar os votos: " + de.getMessage(), de);
		}catch (Exception e) {
			logger.error("Erro ao contabilizar os votos: " + e.getMessage(), e);
		}
		return retorno;
	}

	private String postaNaFilaResultadoVotacao(ResultadoVotacao resultado) {
		String mensagem = montaMensagem(resultado);
		rabbitMQService.enviaMensagem("resultado-votacao", mensagem);
		return mensagem;
	}

	private ResultadoVotacao contabilizaVotosDaSessao(Sessao sessao) {
		ResultadoVotacao resultado = new ResultadoVotacao();
		List<Votacao> votos = votacaoRepo.findBySessaoIdSessao(sessao.getIdSessao());
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
		resultado.setSessaoId(sessao.getIdSessao());
		return resultado;
	}
	private String montaMensagem(ResultadoVotacao resultado) {
		Gson gson = new Gson();
		String json = gson.toJson(resultado);
		logger.info("Mensagem JSON gerada: {}", json);
		return json;
	}


	private void encerraSessaoParaContagemDeVotos(Sessao sessao) {
		votacaoService.encerrarSessao(sessao);
		logger.info("Sessão de votação encerrada para contagem de votos");
	}	
}
