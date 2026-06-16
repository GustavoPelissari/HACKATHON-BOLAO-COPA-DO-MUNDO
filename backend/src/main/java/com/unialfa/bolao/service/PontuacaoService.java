package com.unialfa.bolao.service;

import com.unialfa.bolao.domain.CriterioPontuacao;
import org.springframework.stereotype.Service;

/**
 * Implementa a regra de pontuacao do bolao (secao 4.1 do edital / RF-031).
 * <p>
 * Dado o palpite P (mandante pm, visitante pv) e o resultado R (mandante rm, visitante rv):
 * <ul>
 *   <li>pm = rm E pv = rv  -> 10 pontos (placar exato);</li>
 *   <li>senao, se sign(pm-pv) = sign(rm-rv) -> 5 pontos (acerto do vencedor/empate);</li>
 *   <li>senao -> 0 pontos.</li>
 * </ul>
 */
@Service
public class PontuacaoService {

    public CriterioPontuacao avaliar(int pm, int pv, int rm, int rv) {
        if (pm == rm && pv == rv) {
            return CriterioPontuacao.PLACAR_EXATO;
        }
        if (Integer.signum(pm - pv) == Integer.signum(rm - rv)) {
            return CriterioPontuacao.VENCEDOR;
        }
        return CriterioPontuacao.NENHUM;
    }
}
