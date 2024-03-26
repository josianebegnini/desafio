package com.sicredi.desafio.exceptions;

public class VotacaoEncerradaException extends RuntimeException {
    public VotacaoEncerradaException() {
        super("A votação está encerrada.");
    }
}
