import { RankingPagina } from "../types/models";
import { api } from "./api";

/** RF-032 a RF-034: ranking geral paginado. */
export async function buscarRanking(pagina = 0) {
    const resposta = await api.get<RankingPagina>("/api/ranking", {
        params: { pagina },
    });
    return resposta.data;
}
