package com.unialfa.bolao.service;

import com.unialfa.bolao.api.dto.RankingDtos.RankingItem;
import com.unialfa.bolao.api.dto.RankingDtos.RankingPagina;
import com.unialfa.bolao.domain.Usuario;
import com.unialfa.bolao.repository.UsuarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/** Monta o ranking geral paginado (RF-032, RF-033, RF-034). */
@Service
public class RankingService {

    /** RF-034: minimo de 50 usuarios por pagina. */
    public static final int TAMANHO_PAGINA = 50;

    private final UsuarioRepository usuarioRepository;

    public RankingService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public RankingPagina montar(int pagina, Usuario usuarioAtual) {
        Pageable pageable = PageRequest.of(Math.max(pagina, 0), TAMANHO_PAGINA);
        Page<Usuario> page = usuarioRepository
                .findAllByOrderByPontuacaoTotalDescPlacaresExatosDescCriadoEmAsc(pageable);

        Long idAtual = usuarioAtual == null ? null : usuarioAtual.getId();

        List<RankingItem> itens = new ArrayList<>();
        int posicaoBase = page.getNumber() * TAMANHO_PAGINA;
        int i = 1;
        for (Usuario u : page.getContent()) {
            boolean ehAtual = idAtual != null && idAtual.equals(u.getId());
            itens.add(RankingItem.de(posicaoBase + i, u, ehAtual));
            i++;
        }

        // RF-033: destaca a posicao do usuario autenticado, mesmo fora da pagina atual.
        RankingItem minhaPosicao = null;
        if (usuarioAtual != null) {
            int posicao = calcularPosicao(usuarioAtual);
            minhaPosicao = RankingItem.de(posicao, usuarioAtual, true);
        }

        return new RankingPagina(itens, page.getNumber(), page.getTotalPages(),
                page.getTotalElements(), minhaPosicao);
    }

    /**
     * Posicao do usuario = quantidade de usuarios estritamente a frente dele
     * (segundo os criterios de desempate da regra 4.4) + 1.
     */
    private int calcularPosicao(Usuario alvo) {
        List<Usuario> todos = usuarioRepository
                .findAllByOrderByPontuacaoTotalDescPlacaresExatosDescCriadoEmAsc(Pageable.unpaged())
                .getContent();

        int posicao = 1;
        for (Usuario u : todos) {
            if (u.getId().equals(alvo.getId())) {
                break;
            }
            posicao++;
        }
        return posicao;
    }
}
