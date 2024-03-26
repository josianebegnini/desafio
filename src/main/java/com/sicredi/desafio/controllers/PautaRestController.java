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

import com.sicredi.desafio.models.Pauta;
import com.sicredi.desafio.services.PautaService;


@RestController
@RequestMapping("/api/pauta/")
public class PautaRestController {
    private PautaService pautaService;

    @Autowired
    public PautaRestController(PautaService pautaService) {
        this.pautaService = pautaService;
    }

    @PostMapping(value = "criar", headers = "Accept=application/json")
    public void criarPauta(@RequestBody Pauta pauta) {
    	pautaService.criarPauta(pauta);
    }

    @GetMapping(value = "listar", headers = "Accept=application/json")
    public List<Pauta> listarPautas() {
        return pautaService.listarPautas();
    }
    
    @GetMapping(value = "listarPorId/{id}", headers = "Accept=application/json")
    public Optional<Pauta> buscarPorId(@PathVariable Long id) {
        return pautaService.buscarPorId(id);
    }

    @PutMapping(value = "atualizar", headers = "Accept=application/json")
    public void atualizarPauta(@RequestBody Pauta pauta) {
    	pautaService.atualizarPauta(pauta);
    }

    @DeleteMapping(value = "remover/{id}", headers = "Accept=application/json")
    public void removerPauta(@PathVariable Long id) {
    	pautaService.removerPautaPorId(id);
    }

    @GetMapping(value = "listarPorTitulo/{titulo}", headers = "Accept=application/json")
    public List<Pauta> listarPorTitulo(@PathVariable String titulo) {
        return pautaService.buscarPorTitulo(titulo);
    }
    @GetMapping(value = "listarAbertas", headers = "Accept=application/json")
    public List<Pauta> listarAbertas() {
    	return pautaService.buscarPautasAbertas();
    }

}
