package com.sicredi.desafio.services;

import java.util.List;
import java.util.Optional;

import org.hibernate.service.spi.ServiceException;
import org.springframework.dao.DataAccessException;
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
		try {
			associadoRepo.save(associado);
		} catch (DataAccessException e) {
			throw new ServiceException("Erro ao criar associado: " + e.getMessage(), e);
		}
    }
	public List<Associado> listarAssociados(){
		try {
			return associadoRepo.findAll();
		} catch (DataAccessException e) {
			throw new ServiceException("Erro ao listar associados: " + e.getMessage(), e);
		}
    }
    public Optional<Associado> buscarPorId(Long id){
    	try {
    		return associadoRepo.findById(id);
        } catch (DataAccessException e) {
            throw new ServiceException("Erro ao buscar associado por ID: " + e.getMessage(), e);
        }

    }
    public void atualizarAssociado(Associado associado){
    	try {
    		associadoRepo.save(associado);
    	} catch (DataAccessException e) {
    		throw new ServiceException("Erro ao atualizar associado: " + e.getMessage(), e);
    	}
    }
    public void removerAssociadoPorId(Long id){
    	try {
    		associadoRepo.deleteById(id);
    	} catch (DataAccessException e) {
    		throw new ServiceException("Erro ao remover associado por ID: " + e.getMessage(), e);
    	}
    }
    public List<Associado> buscarPorCpf(String cpf){
    	try {
    		return associadoRepo.findByCpf(cpf);
    	} catch (DataAccessException e) {
    		throw new ServiceException("Erro ao buscar associado por CPF: " + e.getMessage(), e);
    	}
    }
}
