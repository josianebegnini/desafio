package com.sicredi.desafio.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.sicredi.desafio.models.Pauta;
import com.sicredi.desafio.repositories.PautaRepository;

@Service
public class PautaService {
	private PautaRepository pautaRepo;
	
    public PautaService(PautaRepository pautaRepo) {
        this.pautaRepo = pautaRepo;
    }
	public void criarPauta(Pauta pauta){
		pautaRepo.save(pauta);
    }
	public List<Pauta> listarPautas(){
        return pautaRepo.findAll();
    }
    public Optional<Pauta> buscarPorId(Long id){
        return pautaRepo.findById(id);
    }
    public void atualizarPauta(Pauta pauta){
    	pautaRepo.save(pauta);
    }
    public void removerPautaPorId(Long id){
    	pautaRepo.deleteById(id);
    }
    public List<Pauta> buscarPorTitulo(String titulo){
        return pautaRepo.findByTitulo(titulo);
    }
    public List<Pauta> buscarPautasAbertas(){
    	return pautaRepo.findByFechada(false);
    }
}
