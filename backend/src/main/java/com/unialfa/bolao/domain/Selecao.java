package com.unialfa.bolao.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Selecao (pais) participante do campeonato (RF-041). */
@Entity
@Table(name = "selecoes")
@Getter
@Setter
@NoArgsConstructor
public class Selecao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80)
    private String nome;

    /** Codigo FIFA de 3 letras (ex.: BRA, ARG). */
    @Column(name = "codigo_fifa", nullable = false, unique = true, length = 3)
    private String codigoFifa;

    /** URL da bandeira/escudo da selecao. */
    @Column(name = "bandeira_url")
    private String bandeiraUrl;

    /** Grupo do qual a selecao participa (ex.: A, B, C...). */
    @Column(length = 2)
    private String grupo;
}
