package com.sicredi.desafio.services;

import java.util.List;
import java.util.Optional;

import org.hibernate.service.spi.ServiceException;
import org.springframework.dao.DataAccessException;
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
		try {
			sessaoRepo.save(sessao);
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
    public void atualizarSessao(Sessao sessao){
    	try {
    		sessaoRepo.save(sessao);
    	} catch (DataAccessException e) {
    		throw new ServiceException("Erro ao atualizar Sessao: " + e.getMessage(), e);
    	}
    }
    public void removerSessaoPorId(Long id){
    	try {
    		sessaoRepo.deleteById(id);
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
