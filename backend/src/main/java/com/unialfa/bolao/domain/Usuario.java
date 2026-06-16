package com.unialfa.bolao.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Usuario do sistema. Pode ter perfil USER (app mobile) ou ADMIN (painel web).
 * Os campos {@code pontuacaoTotal} e {@code placaresExatos} sao desnormalizados
 * para acelerar a montagem do ranking (RF-032, RF-034).
 */
@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nome;

    @Column(nullable = false, unique = true, length = 160)
    private String email;

    @Column(nullable = false)
    private String senha;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Perfil perfil = Perfil.USER;

    @Column(nullable = false)
    private boolean bloqueado = false;

    @Column(name = "pontuacao_total", nullable = false)
    private int pontuacaoTotal = 0;

    @Column(name = "placares_exatos", nullable = false)
    private int placaresExatos = 0;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @Column(name = "ultimo_acesso")
    private LocalDateTime ultimoAcesso;

    @PrePersist
    void aoCriar() {
        this.criadoEm = LocalDateTime.now();
    }

    public boolean isAdmin() {
        return perfil == Perfil.ADMIN;
    }
}
