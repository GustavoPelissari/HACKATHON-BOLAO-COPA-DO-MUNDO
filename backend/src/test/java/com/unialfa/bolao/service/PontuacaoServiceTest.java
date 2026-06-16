package com.unialfa.bolao.service;

import com.unialfa.bolao.domain.CriterioPontuacao;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** Valida a regra de pontuacao da secao 4.1 do edital. */
class PontuacaoServiceTest {

    private final PontuacaoService service = new PontuacaoService();

    @Test
    void placarExatoVale10() {
        // palpite 2x1, resultado 2x1
        assertEquals(CriterioPontuacao.PLACAR_EXATO, service.avaliar(2, 1, 2, 1));
        assertEquals(10, service.avaliar(2, 1, 2, 1).getPontos());
    }

    @Test
    void acertoDoVencedorVale5() {
        // palpite 3x1 (mandante vence), resultado 2x0 (mandante vence)
        assertEquals(CriterioPontuacao.VENCEDOR, service.avaliar(3, 1, 2, 0));
        assertEquals(5, service.avaliar(3, 1, 2, 0).getPontos());
    }

    @Test
    void acertoDoEmpateVale5() {
        // palpite 1x1, resultado 2x2 (ambos empate, placar diferente)
        assertEquals(CriterioPontuacao.VENCEDOR, service.avaliar(1, 1, 2, 2));
    }

    @Test
    void erroTotalVale0() {
        // palpite mandante vence, resultado visitante vence
        assertEquals(CriterioPontuacao.NENHUM, service.avaliar(2, 0, 0, 1));
        assertEquals(0, service.avaliar(2, 0, 0, 1).getPontos());
    }
}
