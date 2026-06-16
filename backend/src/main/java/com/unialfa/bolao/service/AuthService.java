package com.unialfa.bolao.service;

import com.unialfa.bolao.api.dto.AuthDtos.*;
import com.unialfa.bolao.domain.Perfil;
import com.unialfa.bolao.domain.Usuario;
import com.unialfa.bolao.exception.NegocioException;
import com.unialfa.bolao.repository.UsuarioRepository;
import com.unialfa.bolao.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/** Cadastro, login e recuperacao de senha (RF-001, RF-002, RF-003). */
@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(UsuarioRepository usuarioRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Transactional
    public LoginResponse cadastrar(CadastroRequest req) {
        if (usuarioRepository.existsByEmailIgnoreCase(req.email())) {
            throw new NegocioException("Ja existe uma conta com este e-mail.");
        }

        Usuario usuario = new Usuario();
        usuario.setNome(req.nome().trim());
        usuario.setEmail(req.email().trim().toLowerCase());
        usuario.setSenha(passwordEncoder.encode(req.senha()));
        usuario.setPerfil(Perfil.USER);

        usuarioRepository.save(usuario);
        return montarLogin(usuario);
    }

    @Transactional
    public LoginResponse login(LoginRequest req) {
        // Lanca BadCredentialsException (tratada como 401) quando invalido.
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.email(), req.senha()));

        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(req.email())
                .orElseThrow(() -> new NegocioException("E-mail ou senha invalidos."));

        if (usuario.isBloqueado()) {
            throw new NegocioException("Conta bloqueada. Procure o administrador.");
        }

        usuario.setUltimoAcesso(LocalDateTime.now());
        return montarLogin(usuario);
    }

    /**
     * Recuperacao de senha (RF-003). No MVP geramos um token/link simbolico.
     * Em producao, dispararia um e-mail. Sempre respondemos de forma neutra para
     * nao revelar quais e-mails existem na base.
     */
    public MensagemResponse recuperarSenha(RecuperarSenhaRequest req) {
        usuarioRepository.findByEmailIgnoreCase(req.email())
                .ifPresent(u -> {
                    // TODO: integrar com servico de e-mail e persistir token de recuperacao.
                    String tokenSimbolico = java.util.UUID.randomUUID().toString();
                    System.out.println("[RECUPERACAO] Link para " + u.getEmail()
                            + ": /redefinir-senha?token=" + tokenSimbolico);
                });

        return new MensagemResponse(
                "Se o e-mail estiver cadastrado, enviaremos um link de recuperacao.");
    }

    private LoginResponse montarLogin(Usuario usuario) {
        String token = jwtService.gerarToken(usuario);
        return new LoginResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getAvatarUrl(),
                usuario.getPerfil().name(),
                token);
    }
}
