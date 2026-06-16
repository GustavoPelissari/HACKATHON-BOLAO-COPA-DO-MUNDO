package com.unialfa.bolao.api.dto;

import com.unialfa.bolao.domain.Selecao;

public record SelecaoResponse(
        Long id,
        String nome,
        String codigoFifa,
        String bandeiraUrl,
        String grupo
) {
    public static SelecaoResponse de(Selecao s) {
        return new SelecaoResponse(s.getId(), s.getNome(), s.getCodigoFifa(),
                s.getBandeiraUrl(), s.getGrupo());
    }
}
