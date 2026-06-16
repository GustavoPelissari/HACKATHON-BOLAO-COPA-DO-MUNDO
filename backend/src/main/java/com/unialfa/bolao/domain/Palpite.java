package com.unialfa.bolao.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Palpite de um usuario para uma partida (RF-020 a RF-024).
 * Um usuario pode ter no maximo um palpite por partida (restricao unica).
 */
@Entity
@Table(
        name = "palpites",
        uniqueConstraints = @UniqueConstraint(name = "uk_palpite_usuario_partida",
                columnNames = {"usuario_id", "partida_id"})
)
@Getter
@Setter
@NoArgsConstructor
public class Palpite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToOne(optional = false)
    @JoinColumn(name = "partida_id")
    private Partida partida;

    @Column(name = "gols_mandante", nullable = false)
    private int golsMandante;

    @Column(name = "gols_visitante", nullable = false)
    private int golsVisitante;

    /** Pontos obtidos apos o encerramento da partida (RF-024). */
    @Column(name = "pontos_obtidos")
    private Integer pontosObtidos;

    /** Criterio aplicado no calculo (placar exato, vencedor ou nenhum). */
    @Enumerated(EnumType.STRING)
    @Column(name = "criterio_aplicado", length = 20)
    private CriterioPontuacao criterioAplicado;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    @PrePersist
    void aoCriar() {
        LocalDateTime agora = LocalDateTime.now();
        this.criadoEm = agora;
        this.atualizadoEm = agora;
    }

    @PreUpdate
    void aoAtualizar() {
        this.atualizadoEm = LocalDateTime.now();
    }
}
