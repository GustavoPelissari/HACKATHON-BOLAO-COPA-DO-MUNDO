import axios from "axios";

/** Extrai uma mensagem amigavel de erros da API (inclui o 422 de regra de negocio). */
export function mensagemDeErro(erro: unknown, padrao = "Algo deu errado. Tente novamente."): string {
    if (axios.isAxiosError(erro)) {
        const dados = erro.response?.data as { mensagem?: string } | undefined;
        if (dados?.mensagem) {
            return dados.mensagem;
        }
        if (erro.code === "ECONNABORTED" || !erro.response) {
            return "Nao foi possivel conectar ao servidor.";
        }
    }
    return padrao;
}
