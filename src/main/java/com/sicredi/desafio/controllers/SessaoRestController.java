package com.sicredi.desafio.controllers;

import java.util.List;
import java.util.Optional;

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

import com.sicredi.desafio.models.Sessao;
import com.sicredi.desafio.services.SessaoService;


@RestController
@RequestMapping("/api/sessao/")
public class SessaoRestController {
    private SessaoService sessaoService;

    @Autowired
    public SessaoRestController(SessaoService sessaoService) {
        this.sessaoService = sessaoService;
    }

    @PostMapping(value = "abrirSessao", headers = "Accept=application/json")
    public ResponseEntity<String> criarSessao(@RequestBody Sessao sessao) {
    	try {
    		sessaoService.criarSessao(sessao);
    		return ResponseEntity.ok("Sessao criada com sucesso.");
    	} catch (Exception e) {
    		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao criar sessão: " + e.getMessage());
    	}
    }
    @GetMapping(value = "listar", headers = "Accept=application/json")
    public List<Sessao> listarSessoes() {
        return sessaoService.listarSessoes();
    }
    
    @GetMapping(value = "listarPorId/{id}", headers = "Accept=application/json")
    public Optional<Sessao> buscarPorId(@PathVariable Long id) {
        return sessaoService.buscarPorId(id);
    }

    @PutMapping(value = "atualizar", headers = "Accept=application/json")
    public ResponseEntity<String> atualizarSessao(@RequestBody Sessao sessao) {
    	try {
    		sessaoService.atualizarSessao(sessao);
    		return ResponseEntity.ok("Sessao atualizada com sucesso.");
    	} catch (Exception e) {
    		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao atualizar sessão: " + e.getMessage());
    	}
    }

    @DeleteMapping(value = "remover/{id}", headers = "Accept=application/json")
    public ResponseEntity<String> removerSessao(@PathVariable Long id) {
    	try {
    		sessaoService.removerSessaoPorId(id);
    		return ResponseEntity.ok("Sessao removida com sucesso.");
    	} catch (Exception e) {
    		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao remover sessão: " + e.getMessage());
    	}
    }

    @GetMapping(value = "listarAbertas", headers = "Accept=application/json")
    public List<Sessao> listarAbertas() {
    	return sessaoService.buscarAbertas();
    }

}
