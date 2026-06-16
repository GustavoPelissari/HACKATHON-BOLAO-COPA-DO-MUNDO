package com.unialfa.bolao.api.dto;

import com.unialfa.bolao.domain.Palpite;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

/** DTOs de palpite (RF-020 a RF-024). */
public class PalpiteDtos {

    public record PalpiteRequest(
            @NotNull(message = "Partida e obrigatoria")
            Long partidaId,

            @NotNull(message = "Gols do mandante sao obrigatorios")
            @PositiveOrZero(message = "Gols nao podem ser negativos")
            Integer golsMandante,

            @NotNull(message = "Gols do visitante sao obrigatorios")
            @PositiveOrZero(message = "Gols nao podem ser negativos")
            Integer golsVisitante
    ) {}

    public record PalpiteResponse(
            Long id,
            PartidaResponse partida,
            int golsMandante,
            int golsVisitante,
            Integer pontosObtidos,
            String criterioAplicado
    ) {
        public static PalpiteResponse de(Palpite p) {
            return new PalpiteResponse(
                    p.getId(),
                    PartidaResponse.de(p.getPartida()),
                    p.getGolsMandante(),
                    p.getGolsVisitante(),
                    p.getPontosObtidos(),
                    p.getCriterioAplicado() == null ? null : p.getCriterioAplicado().getDescricao());
        }
    }
}
