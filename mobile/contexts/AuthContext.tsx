import AsyncStorage from "@react-native-async-storage/async-storage";
import { createContext, useCallback, useContext, useEffect, useState } from "react";
import { api, TOKEN_KEY } from "../services/api";
import { buscarMeuPerfil, logar as logarService } from "../services/authService";
import { Usuario } from "../types/models";

// 1. Tudo que o contexto de autenticacao expoe aos consumidores.
type AuthContextData = {
    usuario: Usuario | null;
    carregando: boolean;
    autenticado: boolean;
    entrar: (email: string, senha: string) => Promise<void>;
    entrarComToken: (token: string) => Promise<void>;
    sair: () => Promise<void>;
    atualizarUsuario: (usuario: Usuario) => void;
    recarregarPerfil: () => Promise<void>;
};

const AuthContext = createContext<AuthContextData | null>(null);

export function AuthProvider({ children }: { children: React.ReactNode }) {
    const [usuario, setUsuario] = useState<Usuario | null>(null);
    const [carregando, setCarregando] = useState(true);

    // Ao iniciar o app, tenta restaurar a sessao a partir do token salvo.
    const restaurarSessao = useCallback(async () => {
        try {
            const token = await AsyncStorage.getItem(TOKEN_KEY);
            if (token) {
                const perfil = await buscarMeuPerfil();
                setUsuario(perfil);
            }
        } catch (erro) {
            // Token invalido/expirado: limpa para forcar novo login.
            await AsyncStorage.removeItem(TOKEN_KEY);
        } finally {
            setCarregando(false);
        }
    }, []);

    useEffect(() => {
        restaurarSessao();
    }, [restaurarSessao]);

    async function entrarComToken(token: string) {
        await AsyncStorage.setItem(TOKEN_KEY, token);
        const perfil = await buscarMeuPerfil();
        setUsuario(perfil);
    }

    async function entrar(email: string, senha: string) {
        const resposta = await logarService(email, senha);
        await entrarComToken(resposta.token);
    }

    async function sair() {
        await AsyncStorage.removeItem(TOKEN_KEY);
        delete api.defaults.headers.common.Authorization;
        setUsuario(null);
    }

    function atualizarUsuario(novo: Usuario) {
        setUsuario(novo);
    }

    async function recarregarPerfil() {
        const perfil = await buscarMeuPerfil();
        setUsuario(perfil);
    }

    return (
        <AuthContext.Provider
            value={{
                usuario,
                carregando,
                autenticado: usuario !== null,
                entrar,
                entrarComToken,
                sair,
                atualizarUsuario,
                recarregarPerfil,
            }}
        >
            {children}
        </AuthContext.Provider>
    );
}

export function useAuth(): AuthContextData {
    const context = useContext(AuthContext);
    if (!context) {
        throw new Error("useAuth deve ser usado dentro de um <AuthProvider>");
    }
    return context;
}
