package com.unialfa.bolao.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/** Partida do campeonato (RF-010 a RF-013, RF-043). */
@Entity
@Table(name = "partidas")
@Getter
@Setter
@NoArgsConstructor
public class Partida {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "selecao_mandante_id")
    private Selecao selecaoMandante;

    @ManyToOne(optional = false)
    @JoinColumn(name = "selecao_visitante_id")
    private Selecao selecaoVisitante;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Fase fase;

    @Column(length = 120)
    private String estadio;

    @Column(length = 2)
    private String grupo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusPartida status = StatusPartida.AGENDADA;

    /** Gols do mandante. Nulo enquanto o resultado nao foi lancado. */
    @Column(name = "gols_mandante")
    private Integer golsMandante;

    /** Gols do visitante. Nulo enquanto o resultado nao foi lancado. */
    @Column(name = "gols_visitante")
    private Integer golsVisitante;

    public boolean possuiResultado() {
        return golsMandante != null && golsVisitante != null;
    }

    /** Indica se a partida ja comecou (bloqueia novos palpites - RF-022). */
    public boolean jaIniciou() {
        return LocalDateTime.now().isAfter(dataHora) || dataHora.isEqual(LocalDateTime.now());
    }
}
