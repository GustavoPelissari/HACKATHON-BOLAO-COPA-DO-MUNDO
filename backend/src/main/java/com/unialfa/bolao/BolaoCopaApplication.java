package com.unialfa.bolao;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Ponto de entrada da aplicacao Bolao da Copa do Mundo 2026.
 * Expoe a API REST (consumida pelo app mobile) e o painel administrativo web.
 */
@SpringBootApplication
public class BolaoCopaApplication {

    public static void main(String[] args) {
        SpringApplication.run(BolaoCopaApplication.class, args);
    }
}
