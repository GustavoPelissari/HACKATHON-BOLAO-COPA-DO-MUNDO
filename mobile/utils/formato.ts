import { StatusPartida } from "../types/models";

/** Formata uma data ISO para "dd/mm aaaa HH:MM". */
export function formatarDataHora(iso: string): string {
    const data = new Date(iso);
    const dia = String(data.getDate()).padStart(2, "0");
    const mes = String(data.getMonth() + 1).padStart(2, "0");
    const hora = String(data.getHours()).padStart(2, "0");
    const min = String(data.getMinutes()).padStart(2, "0");
    return `${dia}/${mes} ${hora}:${min}`;
}

export function rotuloStatus(status: StatusPartida): string {
    switch (status) {
        case "AGENDADA":
            return "Agendada";
        case "EM_ANDAMENTO":
            return "Ao vivo";
        case "ENCERRADA":
            return "Encerrada";
    }
}
