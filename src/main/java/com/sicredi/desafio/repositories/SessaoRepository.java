package com.sicredi.desafio.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sicredi.desafio.models.Sessao;

@Repository
public interface SessaoRepository extends JpaRepository<Sessao, Long>{
	List<Sessao> findByFechada(boolean fechada);
	Optional<Sessao> findById(Long idSessao);

}
