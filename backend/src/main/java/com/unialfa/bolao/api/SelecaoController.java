package com.unialfa.bolao.api;

import com.unialfa.bolao.api.dto.SelecaoResponse;
import com.unialfa.bolao.service.SelecaoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Selecoes participantes (consulta publica para o app). */
@RestController
@RequestMapping("/api/selecoes")
public class SelecaoController {

    private final SelecaoService selecaoService;

    public SelecaoController(SelecaoService selecaoService) {
        this.selecaoService = selecaoService;
    }

    @GetMapping
    public List<SelecaoResponse> listar() {
        return selecaoService.listar().stream()
                .map(SelecaoResponse::de)
                .toList();
    }
}
