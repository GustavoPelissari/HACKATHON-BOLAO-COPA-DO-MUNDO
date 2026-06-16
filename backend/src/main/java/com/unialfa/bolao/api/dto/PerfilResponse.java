package com.unialfa.bolao.api.dto;

import com.unialfa.bolao.domain.Usuario;

/** Dados do perfil do usuario autenticado. */
public record PerfilResponse(
        Long id,
        String nome,
        String email,
        String avatarUrl,
        int pontuacaoTotal,
        int placaresExatos
) {
    public static PerfilResponse de(Usuario u) {
        return new PerfilResponse(
                u.getId(), u.getNome(), u.getEmail(), u.getAvatarUrl(),
                u.getPontuacaoTotal(), u.getPlacaresExatos());
    }
}
