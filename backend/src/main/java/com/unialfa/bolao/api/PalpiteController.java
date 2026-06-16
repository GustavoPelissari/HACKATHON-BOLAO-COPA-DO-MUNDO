package com.unialfa.bolao.api;

import com.unialfa.bolao.api.dto.PalpiteDtos.PalpiteRequest;
import com.unialfa.bolao.api.dto.PalpiteDtos.PalpiteResponse;
import com.unialfa.bolao.security.UsuarioDetails;
import com.unialfa.bolao.service.PalpiteService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Palpites do usuario autenticado (RF-020 a RF-024). */
@RestController
@RequestMapping("/api/palpites")
public class PalpiteController {

    private final PalpiteService palpiteService;

    public PalpiteController(PalpiteService palpiteService) {
        this.palpiteService = palpiteService;
    }

    /** Lista os palpites do usuario, com pontuacao quando a partida ja encerrou (RF-023, RF-024). */
    @GetMapping("/meus")
    public List<PalpiteResponse> meus(@AuthenticationPrincipal UsuarioDetails principal) {
        return palpiteService.meusPalpites(principal.getUsuario()).stream()
                .map(PalpiteResponse::de)
                .toList();
    }

    /** Registra ou edita um palpite (RF-020, RF-021). Bloqueado apos o inicio (RF-022). */
    @PostMapping
    public PalpiteResponse registrar(@AuthenticationPrincipal UsuarioDetails principal,
                                     @Valid @RequestBody PalpiteRequest req) {
        return PalpiteResponse.de(palpiteService.registrarOuEditar(principal.getUsuario(), req));
    }
}
