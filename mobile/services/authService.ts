import { Usuario } from "../types/models";
import { api } from "./api";

type LoginResposta = {
    id: number;
    nome: string;
    email: string;
    avatarUrl: string | null;
    perfil: string;
    token: string;
};

/** RF-001: cadastro de usuario. Retorna o token JWT ja autenticado. */
export async function cadastrar(nome: string, email: string, senha: string) {
    const resposta = await api.post<LoginResposta>("/api/auth/cadastro", {
        nome,
        email,
        senha,
    });
    return resposta.data;
}

/** RF-002: login por e-mail e senha. */
export async function logar(email: string, senha: string) {
    const resposta = await api.post<LoginResposta>("/api/auth/login", {
        email,
        senha,
    });
    return resposta.data;
}

/** RF-003: solicita recuperacao de senha. */
export async function recuperarSenha(email: string) {
    const resposta = await api.post<{ mensagem: string }>("/api/auth/recuperar-senha", {
        email,
    });
    return resposta.data.mensagem;
}

/** RF-004: dados do perfil autenticado. */
export async function buscarMeuPerfil() {
    const resposta = await api.get<Usuario>("/api/usuarios/me");
    return resposta.data;
}

/** RF-005: edita nome de exibicao e avatar. */
export async function atualizarPerfil(nome: string, avatarUrl: string | null) {
    const resposta = await api.put<Usuario>("/api/usuarios/me", { nome, avatarUrl });
    return resposta.data;
}

/** RF-006: exclusao de conta (LGPD). */
export async function excluirConta() {
    await api.delete("/api/usuarios/me");
}
