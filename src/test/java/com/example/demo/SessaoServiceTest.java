package com.example.demo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.ContextConfiguration;

import com.sicredi.desafio.models.Pauta;
import com.sicredi.desafio.models.Sessao;
import com.sicredi.desafio.repositories.SessaoRepository;
import com.sicredi.desafio.services.PautaService;
import com.sicredi.desafio.services.SessaoService;

@ContextConfiguration(classes = TestConfig.class)
public class SessaoServiceTest {

    @Mock
    private SessaoRepository sessaoRepository;

    @Mock
    private PautaService pautaService;

    @InjectMocks
    private SessaoService sessaoService;

    @Test
    public void testAbrirSessaoVotacao() {
        // Configuração do teste
        Sessao sessao = new Sessao();
        sessao.setIdSessao(1L);
        sessao.setFechada(false);
        sessao.setDuracao(60); // 60 minutos de duração
        sessao.setPauta(new Pauta());

        Pauta pauta = new Pauta();
        pauta.setIdPauta(1L);
        pauta.setFechada(false);

        LocalDateTime now = LocalDateTime.now();

        // Mocking dos métodos
        when(pautaService.atualizarPauta(any(Pauta.class))).thenReturn(pauta);
        when(sessaoRepository.save(any(Sessao.class))).thenReturn(sessao);

        // Execução do método a ser testado
        sessaoService.abrirSessaoVotacao(sessao);

        // Verificações
        verify(pautaService, times(1)).atualizarPauta(any(Pauta.class));
        verify(sessaoRepository, times(1)).save(any(Sessao.class));

        assertThat(sessao.isFechada()).isFalse();
        assertThat(sessao.getDtSessao()).isNotNull();
        assertThat(sessao.getPauta()).isEqualTo(pauta);
    }
}