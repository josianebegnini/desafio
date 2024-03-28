package com.sicredi.desafio.services;

import java.util.List;
import java.util.Optional;

import org.hibernate.service.spi.ServiceException;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.sicredi.desafio.models.Sessao;
import com.sicredi.desafio.repositories.SessaoRepository;

@Service
public class SessaoService {
	private SessaoRepository sessaoRepo;
	
    public SessaoService(SessaoRepository sessaoRepo) {
    	this.sessaoRepo = sessaoRepo;
    }
	public ResponseEntity<Object> criarSessao(Sessao sessao){
		try {
			if(sessao.getDuracao()<=0) {
				sessao.setDuracao(2);
			}
			sessao.setFechada(false);
			sessaoRepo.save(sessao);
			return ResponseEntity.ok().build();
		} catch (DataAccessException e) {
			throw new ServiceException("Erro ao criar Sessao: " + e.getMessage(), e);
		}
    }
	public List<Sessao> listarSessoes(){
		try {
			return sessaoRepo.findAll();
		} catch (DataAccessException e) {
			throw new ServiceException("Erro ao listar Sessoes: " + e.getMessage(), e);
		}
    }
    public Optional<Sessao> buscarPorId(Long id){
    	try {
    		return sessaoRepo.findById(id);
    	} catch (DataAccessException e) {
    		throw new ServiceException("Erro ao buscar Sessao por ID: " + e.getMessage(), e);
    	}
    }
    public ResponseEntity<Object> atualizarSessao(Sessao sessao){
    	try {
    		sessaoRepo.save(sessao);
    		return ResponseEntity.ok().build();
    	} catch (DataAccessException e) {
    		throw new ServiceException("Erro ao atualizar Sessao: " + e.getMessage(), e);
    	}
    }
    public ResponseEntity<Object> removerSessaoPorId(Long id){
    	try {
    		sessaoRepo.deleteById(id);
    		return ResponseEntity.ok().build();
    	} catch (DataAccessException e) {
    		throw new ServiceException("Erro ao remover Sessao: " + e.getMessage(), e);
    	}
    }
    public List<Sessao> buscarAbertas(){
    	try {
    		return sessaoRepo.findByFechada(false);
    	} catch (DataAccessException e) {
    		throw new ServiceException("Erro ao buscar Sessoes abertas: " + e.getMessage(), e);
    	}
    }
}
