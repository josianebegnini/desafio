package com.sicredi.desafio.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sicredi.desafio.models.Associado;

@Repository
public interface AssociadoRepository extends JpaRepository<Associado, Long>{
	List<Associado> findByCpf(String cpf);

}
