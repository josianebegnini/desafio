package com.sicredi.desafio.services;

import java.text.Normalizer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.sicredi.desafio.exceptions.VotacaoException;
import com.sicredi.desafio.models.Votacao;

@Service
public class AssociadoExternoService {
	 private final String SERVICO_EXETERNO_URL = "https://user-info.herokuapp.com/users/";
	 private static final Logger logger = LogManager.getLogger(AssociadoExternoService.class);
	 
	 public boolean chamaServicoExternoAssociadoPossuiPermissaoParaVotar(Votacao votacao) throws Exception {
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
}
