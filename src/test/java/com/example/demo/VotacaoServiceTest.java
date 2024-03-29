package com.example.demo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.ContextConfiguration;

import com.sicredi.desafio.exceptions.VotacaoEncerradaException;
import com.sicredi.desafio.models.Associado;
import com.sicredi.desafio.models.Sessao;
import com.sicredi.desafio.models.Votacao;
import com.sicredi.desafio.repositories.VotacaoRepository;
import com.sicredi.desafio.services.AssociadoService;
import com.sicredi.desafio.services.MessageProducer;
import com.sicredi.desafio.services.SessaoService;
import com.sicredi.desafio.services.VotacaoService;

@ContextConfiguration(classes = TestConfig.class)
public class VotacaoServiceTest {

    @Mock
    private VotacaoRepository votacaoRepository;

    @Mock
    private SessaoService sessaoService;

    @Mock
    private AssociadoService associadoService;

    @Mock
    private MessageProducer messageProducer;

    @InjectMocks
    private VotacaoService votacaoService;

    
    @Test
    public void testAssociadoPossuiPermissaoParaVotar() throws Exception {
        // Simulando um associado com permissão para votar
        Votacao votacao = new Votacao();
        Associado associado = new Associado();
        associado.setCpf("12345678901");
        votacao.setAssociado(associado); // CPF válido

        when(sessaoService.buscarPorId(anyLong())).thenReturn(Optional.of(new Sessao())); // Mocking sessão
        when(votacaoRepository.findByAssociadoCpf(anyString())).thenReturn(null); // Associado não votou ainda

        boolean result = votacaoService.associadoPossuiPermissaoParaVotar(votacao);
        assertThat(result).isTrue();
    }

    @Test
    public void testAssociadoJaVotou() throws Exception {
        // Simulando um associado que já votou
        Votacao votacao = new Votacao();
        Associado associado = new Associado();
        associado.setCpf("12345678901");
        votacao.setAssociado(associado); // CPF válido

        when(votacaoRepository.findByAssociadoCpf(anyString())).thenReturn(List.of(votacao)); // Associado já votou

        boolean result = votacaoService.associadoJaVotou(votacao);
        assertThat(result).isTrue();
    }

    @Test
    public void testVotar_SessaoAberta() throws Exception {
        // Configurar comportamento simulado do objeto de sessão
        Sessao sessao = new Sessao();
        sessao.setIdSessao(1L);
        sessao.setFechada(false);
        when(sessaoService.buscarPorId(sessao.getIdSessao())).thenReturn(Optional.of(sessao));

        // Configurar comportamento simulado do objeto associado
        Associado associado = new Associado();
        associado.setCpf("12345678901");
        when(associadoService.buscarPorCpf(associado.getCpf())).thenReturn(associado);

        // Configurar comportamento simulado do repositório de votação
        when(votacaoRepository.save(any(Votacao.class))).thenReturn(new Votacao());

        // Chamar o método que será testado
        Votacao votacao = new Votacao();
        votacao.setSessao(sessao);
        votacao.setAssociado(associado);
        votacaoService.votar(votacao);

        // Verificar se o voto foi registrado corretamente
        verify(votacaoRepository, times(1)).save(votacao);
    }

    @Test
    public void testVotar_SessaoEncerrada() {
        // Configurar comportamento simulado do objeto de sessão
        Sessao sessao = new Sessao();
        sessao.setIdSessao(1L);
        sessao.setFechada(true);
        when(sessaoService.buscarPorId(sessao.getIdSessao())).thenReturn(Optional.of(sessao));

        // Chamar o método que será testado e verificar se ele lança VotacaoEncerradaException
        Assertions.assertThrows(VotacaoEncerradaException.class, () -> {
            votacaoService.votar(new Votacao());
        });
    }
    
    @Test
    public void testVotar() throws Exception {
        // Simulando uma votação válida
        Votacao votacao = new Votacao();

        when(sessaoService.buscarPorId(anyLong())).thenReturn(Optional.of(new Sessao())); // Mocking sessão
        when(associadoService.buscarPorCpf(anyString())).thenReturn(new Associado()); // Associado válido
        when(votacaoRepository.findByAssociadoCpf(anyString())).thenReturn(null); // Associado não votou ainda

        votacaoService.votar(votacao); // Verifica se a execução ocorre sem exceções
    }
}