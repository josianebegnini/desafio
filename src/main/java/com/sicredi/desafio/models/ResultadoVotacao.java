package com.sicredi.desafio.models;

public class ResultadoVotacao {
	private int totalPositivo;
	private int totalNegativo;
	private Long sessaoId;
	public int getTotalPositivo() {
		return totalPositivo;
	}
	public void setTotalPositivo(int totalPositivo) {
		this.totalPositivo = totalPositivo;
	}
	public int getTotalNegativo() {
		return totalNegativo;
	}
	public void setTotalNegativo(int totalNegativo) {
		this.totalNegativo = totalNegativo;
	}
	public Long getSessaoId() {
		return sessaoId;
	}
	public void setSessaoId(Long sessaoId) {
		this.sessaoId = sessaoId;
	}
}
