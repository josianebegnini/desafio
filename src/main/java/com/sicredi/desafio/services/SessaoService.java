package com.sicredi.desafio.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.sicredi.desafio.models.Sessao;
import com.sicredi.desafio.repositories.SessaoRepository;

@Service
public class SessaoService {
	private SessaoRepository sessaoRepo;
	
    public SessaoService(SessaoRepository sessaoRepo) {
        this.sessaoRepo = sessaoRepo;
    }
	public void criarSessao(Sessao sessao){
		sessaoRepo.save(sessao);
    }
	public List<Sessao> listarSessoes(){
        return sessaoRepo.findAll();
    }
    public Optional<Sessao> buscarPorId(Long id){
        return sessaoRepo.findById(id);
    }
    public void atualizarSessao(Sessao sessao){
    	sessaoRepo.save(sessao);
    }
    public void removerSessaoPorId(Long id){
    	sessaoRepo.deleteById(id);
    }
    public List<Sessao> buscarAbertas(){
        return sessaoRepo.findByFechada(false);
    }
}
