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

import com.sicredi.desafio.models.Associado;
import com.sicredi.desafio.services.AssociadoService;

@RestController
@RequestMapping("/api/associado/")
public class AssociadoRestController {
    private AssociadoService associadoService;

    @Autowired
    public AssociadoRestController(AssociadoService associadoService) {
        this.associadoService = associadoService;
    }

    @PostMapping(value = "criar", headers = "Accept=application/json")
    public void criarAssociado(@RequestBody Associado associado) {
    	associadoService.criarAssociado(associado);
    }

    @GetMapping(value = "listar", headers = "Accept=application/json")
    public List<Associado> listarAssociados() {
        return associadoService.listarAssociados();
    }
    
    @GetMapping(value = "listarPorId/{id}", headers = "Accept=application/json")
    public Optional<Associado> buscarPorId(@PathVariable Long id) {
        return associadoService.buscarPorId(id);
    }

    @PutMapping(value = "atualizar", headers = "Accept=application/json")
    public void atualizarAssociado(@RequestBody Associado associado) {
    	associadoService.atualizarAssociado(associado);
    }

    @DeleteMapping(value = "remover/{id}", headers = "Accept=application/json")
    public void removerAssociado(@PathVariable Long id) {
    	associadoService.removerAssociadoPorId(id);
    }

    @GetMapping(value = "listarPorCpf/{cpf}", headers = "Accept=application/json")
    public List<Associado> listarPorCpf(@PathVariable String cpf) {
        return associadoService.buscarPorCpf(cpf);
    }
}
