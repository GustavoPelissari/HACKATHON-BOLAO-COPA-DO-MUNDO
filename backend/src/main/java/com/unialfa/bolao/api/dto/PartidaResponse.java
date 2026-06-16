package com.unialfa.bolao.api.dto;

import com.unialfa.bolao.domain.Partida;

import java.time.LocalDateTime;

/** Detalhe de uma partida (RF-011). */
public record PartidaResponse(
        Long id,
        SelecaoResponse mandante,
        SelecaoResponse visitante,
        LocalDateTime dataHora,
        String fase,
        String faseDescricao,
        String estadio,
        String grupo,
        String status,
        Integer golsMandante,
        Integer golsVisitante,
        boolean abertaParaPalpite
) {
    public static PartidaResponse de(Partida p) {
        return new PartidaResponse(
                p.getId(),
                SelecaoResponse.de(p.getSelecaoMandante()),
                SelecaoResponse.de(p.getSelecaoVisitante()),
                p.getDataHora(),
                p.getFase().name(),
                p.getFase().getDescricao(),
                p.getEstadio(),
                p.getGrupo(),
                p.getStatus().name(),
                p.getGolsMandante(),
                p.getGolsVisitante(),
                p.getStatus() == com.unialfa.bolao.domain.StatusPartida.AGENDADA && !p.jaIniciou()
        );
    }
}
