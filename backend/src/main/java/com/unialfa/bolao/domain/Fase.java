package com.unialfa.bolao.domain;

/**
 * Fase da competicao a qual uma partida pertence.
 * Formato da Copa de 2026: 48 selecoes, 12 grupos, e mata-mata a partir dos 16-avos.
 * A ordem das constantes segue a sequencia cronologica do torneio.
 */
public enum Fase {
    GRUPOS("Fase de Grupos"),
    DEZESSEIS_AVOS("16-avos de final"),
    OITAVAS("Oitavas de final"),
    QUARTAS("Quartas de final"),
    SEMI("Semifinal"),
    TERCEIRO_LUGAR("Disputa de 3o lugar"),
    FINAL("Final");

    private final String descricao;

    Fase(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
