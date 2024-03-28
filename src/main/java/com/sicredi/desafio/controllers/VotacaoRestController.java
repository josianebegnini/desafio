package com.sicredi.desafio.controllers;

import java.util.List;
import java.util.Optional;

import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sicredi.desafio.exceptions.VotacaoEncerradaException;
import com.sicredi.desafio.models.ResultadoVotacao;
import com.sicredi.desafio.models.Sessao;
import com.sicredi.desafio.models.Votacao;
import com.sicredi.desafio.services.SessaoService;
import com.sicredi.desafio.services.VotacaoService;


@RestController
@RequestMapping("/api/votacao/")
public class VotacaoRestController {
    private VotacaoService votacaoService;
    private SessaoService sessaoService;

    @Autowired
    public VotacaoRestController(VotacaoService votacaoService, SessaoService sessaoService) {
        this.votacaoService = votacaoService;
        this.sessaoService = sessaoService;
    }

    @PostMapping(value = "votar", headers = "Accept=application/json")
    public ResponseEntity<?> votar(@RequestBody Votacao votacao) {
    	 try {
             votacaoService.votar(votacao);
             return ResponseEntity.ok("Voto registrado com sucesso.");
         } catch (VotacaoEncerradaException e) {
             return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
         } catch (ServiceException e) {
             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao votar: " + e.getMessage());
         }
    }

    @GetMapping("/contabilizar/{idSessao}")
    public  ResponseEntity<?> contabilizarVotacao(@PathVariable Long idSessao) {
    	try {
    		Sessao sessao = sessaoService.buscarPorId(idSessao).orElse(null);

    		if (sessao == null) {
    			return ResponseEntity.notFound().build();
    		}

    		if (!sessao.isFechada()) {
    			return ResponseEntity.badRequest().body("A sessão de votação não está fechada.");
    		}

    		ResultadoVotacao resultado = votacaoService.contabilizar(sessao);

    		return ResponseEntity.ok(resultado);
    	} catch (ServiceException e) {
    		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao contabilizar votos: " + e.getMessage());
    	}
        
    }

    
    @GetMapping(value = "listar", headers = "Accept=application/json")
    public List<Votacao> listarVotacoes() {
        return votacaoService.listarVotacoes();
    }
    
    @GetMapping(value = "listarPorId/{id}", headers = "Accept=application/json")
    public Optional<Votacao> buscarPorId(@PathVariable Long id) {
        return votacaoService.buscarPorId(id);
    }

    @PutMapping(value = "atualizar", headers = "Accept=application/json")
    public void atualizarVotacao(@RequestBody Votacao votacao) {
    	votacaoService.atualizarVotacao(votacao);
    }

    @DeleteMapping(value = "remover/{id}", headers = "Accept=application/json")
    public void removerVotacao(@PathVariable Long id) {
    	votacaoService.removerVotacaoPorId(id);
    }


}
