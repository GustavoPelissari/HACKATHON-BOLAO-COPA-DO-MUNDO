import { Partida, StatusPartida } from "../types/models";
import { api } from "./api";

type FiltroPartida = {
    fase?: string;
    status?: StatusPartida;
};

/** RF-010 / RF-012: lista partidas, com filtro opcional por fase e status. */
export async function buscarPartidas(filtro: FiltroPartida = {}) {
    const resposta = await api.get<Partida[]>("/api/partidas", {
        params: filtro,
    });
    return resposta.data;
}

/** RF-013: proximas partidas abertas a palpite. */
export async function buscarProximasPartidas() {
    const resposta = await api.get<Partida[]>("/api/partidas/proximas");
    return resposta.data;
}

/** RF-011: detalhe de uma partida. */
export async function buscarPartidaPorId(id: number) {
    const resposta = await api.get<Partida>(`/api/partidas/${id}`);
    return resposta.data;
}
