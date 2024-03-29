package com.sicredi.desafio.services;

import java.util.List;
import java.util.Optional;

import org.hibernate.service.spi.ServiceException;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.sicredi.desafio.models.Pauta;
import com.sicredi.desafio.repositories.PautaRepository;

@Service
public class PautaService {
	private PautaRepository pautaRepo;
	
    public PautaService(PautaRepository pautaRepo) {
    	this.pautaRepo = pautaRepo;
    }
	public ResponseEntity<Object> criarPauta(Pauta pauta){
		try {
			pautaRepo.save(pauta);
			return ResponseEntity.ok().build();
		} catch (DataAccessException e) {
			throw new ServiceException("Erro ao cadastrar Pauta: " + e.getMessage(), e);
		}
    }
	public List<Pauta> listarPautas(){
		try {
			return pautaRepo.findAll();
		} catch (DataAccessException e) {
			throw new ServiceException("Erro ao listar Pautas: " + e.getMessage(), e);
		}
    }
    public Optional<Pauta> buscarPorId(Long id){
    	try {
    		return pautaRepo.findById(id);
    	} catch (DataAccessException e) {
    		throw new ServiceException("Erro ao buscar Pauta por ID: " + e.getMessage(), e);
    	}
    }
    public void atualizarPauta(Pauta pauta){
    	try {
    		pautaRepo.save(pauta);
    	} catch (DataAccessException e) {
    		throw new ServiceException("Erro ao atualizar pauta: " + e.getMessage(), e);
    	}
    }
    public void removerPautaPorId(Long id){
    	try {
    		pautaRepo.deleteById(id);
    	} catch (DataAccessException e) {
    		throw new ServiceException("Erro ao remover pauta por ID: " + e.getMessage(), e);
    	}
    }
    public List<Pauta> buscarPorTitulo(String titulo){
    	try {
    		return pautaRepo.findByTitulo(titulo);
    	} catch (DataAccessException e) {
    		throw new ServiceException("Erro ao buscar pauta por titulo: " + e.getMessage(), e);
    	}
    }
    public List<Pauta> buscarPautasAbertas(){
    	try {
    		return pautaRepo.findByFechada(false);
    	} catch (DataAccessException e) {
    		throw new ServiceException("Erro ao buscar pautas em aberto: " + e.getMessage(), e);
    	}
    }
}
