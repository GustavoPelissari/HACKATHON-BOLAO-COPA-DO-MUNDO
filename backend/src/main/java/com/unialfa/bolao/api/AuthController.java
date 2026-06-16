package com.unialfa.bolao.api;

import com.unialfa.bolao.api.dto.AuthDtos.*;
import com.unialfa.bolao.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/** Endpoints de autenticacao e conta (RF-001 a RF-003). */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/cadastro")
    public ResponseEntity<LoginResponse> cadastrar(@Valid @RequestBody CadastroRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.cadastrar(req));
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest req) {
        return authService.login(req);
    }

    @PostMapping("/recuperar-senha")
    public MensagemResponse recuperarSenha(@Valid @RequestBody RecuperarSenhaRequest req) {
        return authService.recuperarSenha(req);
    }
}
