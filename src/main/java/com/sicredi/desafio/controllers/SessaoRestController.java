package com.sicredi.desafio.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
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

    @PostMapping(value = "criar", headers = "Accept=application/json")
    public void criarSessao(@RequestBody Sessao sessao) {
    	sessaoService.criarSessao(sessao);
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
    public void atualizarSessao(@RequestBody Sessao sessao) {
    	sessaoService.atualizarSessao(sessao);
    }

    @DeleteMapping(value = "remover/{id}", headers = "Accept=application/json")
    public void removerSessao(@PathVariable Long id) {
    	sessaoService.removerSessaoPorId(id);
    }

    @GetMapping(value = "listarAbertas", headers = "Accept=application/json")
    public List<Sessao> listarAbertas() {
    	return sessaoService.buscarAbertas();
    }

}
