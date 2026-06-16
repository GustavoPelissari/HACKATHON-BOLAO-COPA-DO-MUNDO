package com.unialfa.bolao.exception;

/** Violacao de regra de negocio. Mapeada para HTTP 422 (RF-022 / 4.2). */
public class NegocioException extends RuntimeException {
    public NegocioException(String mensagem) {
        super(mensagem);
    }
}
