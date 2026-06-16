package com.unialfa.bolao.domain;

/** Criterio aplicado ao pontuar um palpite (RF-031). */
public enum CriterioPontuacao {
    PLACAR_EXATO("Placar exato", 10),
    VENCEDOR("Acerto do vencedor/empate", 5),
    NENHUM("Erro total", 0);

    private final String descricao;
    private final int pontos;

    CriterioPontuacao(String descricao, int pontos) {
        this.descricao = descricao;
        this.pontos = pontos;
    }

    public String getDescricao() {
        return descricao;
    }

    public int getPontos() {
        return pontos;
    }
}
