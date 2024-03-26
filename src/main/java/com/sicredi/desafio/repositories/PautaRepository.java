package com.sicredi.desafio.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sicredi.desafio.models.Pauta;

@Repository
public interface PautaRepository extends JpaRepository<Pauta, Long>{
	List<Pauta> findByTitulo(String titulo);
	List<Pauta> findByFechada(boolean fechado);

}
