package com.unialfa.bolao.api.dto;

import com.unialfa.bolao.domain.Usuario;

import java.util.List;

/** DTOs do ranking geral (RF-032, RF-033). */
public class RankingDtos {

    public record RankingItem(
            int posicao,
            Long usuarioId,
            String nome,
            String avatarUrl,
            int pontuacaoTotal,
            int placaresExatos,
            boolean ehUsuarioAtual
    ) {
        public static RankingItem de(int posicao, Usuario u, boolean ehUsuarioAtual) {
            return new RankingItem(posicao, u.getId(), u.getNome(), u.getAvatarUrl(),
                    u.getPontuacaoTotal(), u.getPlacaresExatos(), ehUsuarioAtual);
        }
    }

    /** Pagina de ranking com a posicao do usuario autenticado destacada (RF-033). */
    public record RankingPagina(
            List<RankingItem> itens,
            int pagina,
            int totalPaginas,
            long totalUsuarios,
            RankingItem minhaPosicao
    ) {}
}
