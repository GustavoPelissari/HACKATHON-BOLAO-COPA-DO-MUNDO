// Tipos de dominio compartilhados pelo app, espelhando os DTOs da API REST.

export type Usuario = {
    id: number;
    nome: string;
    email: string;
    avatarUrl: string | null;
    pontuacaoTotal: number;
    placaresExatos: number;
};

export type Selecao = {
    id: number;
    nome: string;
    codigoFifa: string;
    bandeiraUrl: string | null;
    grupo: string | null;
};

export type StatusPartida = "AGENDADA" | "EM_ANDAMENTO" | "ENCERRADA";

export type Partida = {
    id: number;
    mandante: Selecao;
    visitante: Selecao;
    dataHora: string;
    fase: string;
    faseDescricao: string;
    estadio: string | null;
    grupo: string | null;
    status: StatusPartida;
    golsMandante: number | null;
    golsVisitante: number | null;
    abertaParaPalpite: boolean;
};

export type Palpite = {
    id: number;
    partida: Partida;
    golsMandante: number;
    golsVisitante: number;
    pontosObtidos: number | null;
    criterioAplicado: string | null;
};

export type RankingItem = {
    posicao: number;
    usuarioId: number;
    nome: string;
    avatarUrl: string | null;
    pontuacaoTotal: number;
    placaresExatos: number;
    ehUsuarioAtual: boolean;
};

export type RankingPagina = {
    itens: RankingItem[];
    pagina: number;
    totalPaginas: number;
    totalUsuarios: number;
    minhaPosicao: RankingItem | null;
};
