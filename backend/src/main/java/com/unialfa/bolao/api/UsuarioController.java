package com.unialfa.bolao.api;

import com.unialfa.bolao.api.dto.AuthDtos.AtualizarPerfilRequest;
import com.unialfa.bolao.api.dto.AuthDtos.MensagemResponse;
import com.unialfa.bolao.api.dto.PerfilResponse;
import com.unialfa.bolao.domain.Usuario;
import com.unialfa.bolao.security.UsuarioDetails;
import com.unialfa.bolao.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/** Perfil do usuario autenticado (RF-004, RF-005, RF-006). */
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/me")
    public PerfilResponse meuPerfil(@AuthenticationPrincipal UsuarioDetails principal) {
        return PerfilResponse.de(principal.getUsuario());
    }

    @PutMapping("/me")
    public PerfilResponse atualizarPerfil(@AuthenticationPrincipal UsuarioDetails principal,
                                          @Valid @RequestBody AtualizarPerfilRequest req) {
        Usuario atualizado = usuarioService.atualizarPerfil(principal.getUsuario(), req);
        return PerfilResponse.de(atualizado);
    }

    @DeleteMapping("/me")
    public MensagemResponse excluirConta(@AuthenticationPrincipal UsuarioDetails principal) {
        usuarioService.excluirConta(principal.getUsuario());
        return new MensagemResponse("Conta excluida com sucesso.");
    }
}
