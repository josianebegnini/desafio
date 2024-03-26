package com.sicredi.desafio.models;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Votacao {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idVotacao")
	private Long idVotacao;
	private Date dtVoto;
	private String voto;
	
	@ManyToOne
    @JoinColumn(name = "sessao_id")
    private Sessao sessao;
	
	@ManyToOne
	@JoinColumn(name = "pauta_id")
	private Pauta pauta;
	
	@ManyToOne
	@JoinColumn(name = "cpf")
	private Associado associado;
	
	public Date getDtVoto() {
		return dtVoto;
	}

	public void setDtVoto(Date dtVoto) {
		this.dtVoto = dtVoto;
	}

	public Sessao getSessao() {
		return sessao;
	}

	public void setSessao(Sessao sessao) {
		this.sessao = sessao;
	}

	public Pauta getPauta() {
		return pauta;
	}

	public void setPauta(Pauta pauta) {
		this.pauta = pauta;
	}

	public Associado getAssociado() {
		return associado;
	}

	public void setAssociado(Associado associado) {
		this.associado = associado;
	}

	public Long getIdVotacao() {
		return idVotacao;
	}

	public void setIdVotacao(Long idVotacao) {
		this.idVotacao = idVotacao;
	}

	public String getVoto() {
		return voto;
	}

	public void setVoto(String voto) {
		this.voto = voto;
	}

}
