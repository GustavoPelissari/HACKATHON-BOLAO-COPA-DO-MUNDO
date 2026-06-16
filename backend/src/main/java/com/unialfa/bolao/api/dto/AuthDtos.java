package com.unialfa.bolao.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** DTOs de autenticacao e conta (RF-001 a RF-006). */
public class AuthDtos {

    public record CadastroRequest(
            @NotBlank(message = "Nome e obrigatorio")
            String nome,

            @NotBlank(message = "E-mail e obrigatorio")
            @Email(message = "E-mail invalido")
            String email,

            @NotBlank(message = "Senha e obrigatoria")
            @Size(min = 6, message = "Senha deve ter no minimo 6 caracteres")
            String senha
    ) {}

    public record LoginRequest(
            @NotBlank(message = "E-mail e obrigatorio")
            @Email(message = "E-mail invalido")
            String email,

            @NotBlank(message = "Senha e obrigatoria")
            String senha
    ) {}

    public record LoginResponse(
            Long id,
            String nome,
            String email,
            String avatarUrl,
            String perfil,
            String token
    ) {}

    public record RecuperarSenhaRequest(
            @NotBlank(message = "E-mail e obrigatorio")
            @Email(message = "E-mail invalido")
            String email
    ) {}

    public record AtualizarPerfilRequest(
            @NotBlank(message = "Nome e obrigatorio")
            String nome,
            String avatarUrl
    ) {}

    public record MensagemResponse(String mensagem) {}
}
