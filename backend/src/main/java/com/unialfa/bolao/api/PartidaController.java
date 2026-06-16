package com.unialfa.bolao.api;

import com.unialfa.bolao.api.dto.PartidaResponse;
import com.unialfa.bolao.domain.Fase;
import com.unialfa.bolao.domain.StatusPartida;
import com.unialfa.bolao.service.PartidaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Partidas e tabela do campeonato (RF-010 a RF-013). Endpoints publicos. */
@RestController
@RequestMapping("/api/partidas")
public class PartidaController {

    private final PartidaService partidaService;

    public PartidaController(PartidaService partidaService) {
        this.partidaService = partidaService;
    }

    /** Lista partidas, opcionalmente filtradas por fase e/ou status (RF-010, RF-012). */
    @GetMapping
    public List<PartidaResponse> listar(@RequestParam(required = false) Fase fase,
                                        @RequestParam(required = false) StatusPartida status) {
        return partidaService.filtrar(fase, status).stream()
                .map(PartidaResponse::de)
                .toList();
    }

    /** Proximas partidas abertas a palpite (RF-013). */
    @GetMapping("/proximas")
    public List<PartidaResponse> proximas() {
        return partidaService.proximas().stream()
                .map(PartidaResponse::de)
                .toList();
    }

    /** Detalhe de uma partida (RF-011). */
    @GetMapping("/{id}")
    public PartidaResponse detalhe(@PathVariable Long id) {
        return PartidaResponse.de(partidaService.buscarPorId(id));
    }
}
