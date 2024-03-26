package com.sicredi.desafio.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sicredi.desafio.models.Sessao;
import com.sicredi.desafio.models.Votacao;

@Repository
public interface VotacaoRepository extends JpaRepository<Votacao, Long>{
	List<Votacao> findByPautaId(Long idPauta);
	List<Votacao> findByAssociadoCpf(String cpf);
	List<Votacao> findBySessao(Long idSessao);
	List<Votacao> findBySessaoIdAndVotoSim(Votacao votacao);
}
