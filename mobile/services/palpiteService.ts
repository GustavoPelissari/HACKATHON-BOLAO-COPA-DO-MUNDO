import { Palpite } from "../types/models";
import { api } from "./api";

/** RF-023 / RF-024: lista os palpites do usuario, com pontuacao quando disponivel. */
export async function buscarMeusPalpites() {
    const resposta = await api.get<Palpite[]>("/api/palpites/meus");
    return resposta.data;
}

/** RF-020 / RF-021: registra ou edita um palpite. */
export async function enviarPalpite(
    partidaId: number,
    golsMandante: number,
    golsVisitante: number
) {
    const resposta = await api.post<Palpite>("/api/palpites", {
        partidaId,
        golsMandante,
        golsVisitante,
    });
    return resposta.data;
}
