package com.sicredi.desafio.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.sicredi.desafio.models.Associado;
import com.sicredi.desafio.repositories.AssociadoRepository;

@Service
public class AssociadoService {
	private AssociadoRepository associadoRepo;
	
    public AssociadoService(AssociadoRepository associadoRepo) {
        this.associadoRepo = associadoRepo;
    }
	public void criarAssociado(Associado associado){
		associadoRepo.save(associado);
    }
	public List<Associado> listarAssociados(){
        return associadoRepo.findAll();
    }
    public Optional<Associado> buscarPorId(Long id){
        return associadoRepo.findById(id);
    }
    public void atualizarAssociado(Associado associado){
    	associadoRepo.save(associado);
    }
    public void removerAssociadoPorId(Long id){
    	associadoRepo.deleteById(id);
    }
    public List<Associado> buscarPorCpf(String cpf){
        return associadoRepo.findByCpf(cpf);
    }
}
