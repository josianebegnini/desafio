package com.sicredi.desafio.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sicredi.desafio.models.Votacao;

@Repository
public interface VotacaoRepository extends JpaRepository<Votacao, Long>{
	List<Votacao> findByAssociadoCpf(String cpf);
	List<Votacao> findBySessaoIdSessao(Long idSessao);
}
