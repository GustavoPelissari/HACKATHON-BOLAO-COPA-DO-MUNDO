package com.unialfa.bolao.exception;

/** Recurso inexistente. Mapeada para HTTP 404. */
public class RecursoNaoEncontradoException extends RuntimeException {
    public RecursoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
