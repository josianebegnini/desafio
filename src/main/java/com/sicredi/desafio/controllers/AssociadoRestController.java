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

    @PostMapping(value = "cadastrar", headers = "Accept=application/json")
    public ResponseEntity<Object> criarAssociado(@RequestBody Associado associado) {
    	try {	
    		associadoService.criarAssociado(associado);
    		return ResponseEntity.ok().build();
    	} catch (Exception e) {
    		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao criar associado: " + e.getMessage());
    	}
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
    public ResponseEntity<Object> atualizarAssociado(@RequestBody Associado associado) {
    	try {
    		associadoService.atualizarAssociado(associado);
    		return ResponseEntity.ok().build();
    	} catch (Exception e) {
    		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao atualizar associado: " + e.getMessage());
    	}
    }

    @DeleteMapping(value = "remover/{id}", headers = "Accept=application/json")
    public ResponseEntity<Object> removerAssociado(@PathVariable Long id) {
    	try {
    		associadoService.removerAssociadoPorId(id);
    		return ResponseEntity.ok().build();
    	} catch (Exception e) {
    		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao remover associado: " + e.getMessage());
    	}
    }

    @GetMapping(value = "listarPorCpf/{cpf}", headers = "Accept=application/json")
    public List<Associado> listarPorCpf(@PathVariable String cpf) {
        return associadoService.buscarPorCpf(cpf);
    }
}
