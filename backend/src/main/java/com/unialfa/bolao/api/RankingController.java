package com.unialfa.bolao.api;

import com.unialfa.bolao.api.dto.RankingDtos.RankingPagina;
import com.unialfa.bolao.domain.Usuario;
import com.unialfa.bolao.security.UsuarioDetails;
import com.unialfa.bolao.service.RankingService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Ranking geral (RF-032 a RF-034). Publico: visitantes podem visualizar.
 * Quando ha token, destaca a posicao do usuario autenticado.
 */
@RestController
@RequestMapping("/api/ranking")
public class RankingController {

    private final RankingService rankingService;

    public RankingController(RankingService rankingService) {
        this.rankingService = rankingService;
    }

    @GetMapping
    public RankingPagina ranking(@RequestParam(defaultValue = "0") int pagina,
                                 @AuthenticationPrincipal UsuarioDetails principal) {
        Usuario atual = principal == null ? null : principal.getUsuario();
        return rankingService.montar(pagina, atual);
    }
}
