package com.sicredi.desafio.models;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Sessao {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idSessao")
	private Long idSessao;
	private String nome;
	private String descricao;
	private LocalDateTime dtSessao;
	private LocalDateTime dtEncerramento;
	private int duracao;
	private boolean fechada;

	@ManyToOne
	@JoinColumn(name = "pauta_id")
	private Pauta pauta;
	
	public Long getIdSessao() {
		return idSessao;
	}
	public void setIdSessao(Long idSessao) {
		this.idSessao = idSessao;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public LocalDateTime getDtSessao() {
		return dtSessao;
	}
	public void setDtSessao(LocalDateTime localDateTime) {
		this.dtSessao = localDateTime;
	}
	public int getDuracao() {
		return duracao;
	}
	public void setDuracao(int duracao) {
		this.duracao = duracao;
	}
	public boolean isFechada() {
		return fechada;
	}
	public void setFechada(boolean fechada) {
		this.fechada = fechada;
	}
	public LocalDateTime getDtEncerramento() {
		return dtEncerramento;
	}
	public void setDtEncerramento(LocalDateTime dtEncerramento) {
		this.dtEncerramento = dtEncerramento;
	}
	public Pauta getPauta() {
		return pauta;
	}
	public void setPauta(Pauta pauta) {
		this.pauta = pauta;
	}
	
}
