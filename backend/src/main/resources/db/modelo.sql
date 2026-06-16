-- ============================================================================
-- Modelo de dados do Bolao Copa do Mundo 2026 (Artefato 3 - MySQL).
--
-- Este script e apenas DOCUMENTACAO/referencia. Em tempo de execucao o schema
-- e criado e mantido automaticamente pelo Hibernate (spring.jpa.hibernate.ddl-auto=update).
-- Use este arquivo para entender a estrutura ou criar o banco manualmente.
-- ============================================================================

CREATE DATABASE IF NOT EXISTS bolao_copa
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE bolao_copa;

CREATE TABLE usuarios (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome            VARCHAR(120)  NOT NULL,
    email           VARCHAR(160)  NOT NULL UNIQUE,
    senha           VARCHAR(255)  NOT NULL,            -- hash BCrypt
    avatar_url      VARCHAR(255),
    perfil          VARCHAR(20)   NOT NULL DEFAULT 'USER', -- USER | ADMIN
    bloqueado       BOOLEAN       NOT NULL DEFAULT FALSE,
    pontuacao_total INT           NOT NULL DEFAULT 0,
    placares_exatos INT           NOT NULL DEFAULT 0,
    criado_em       DATETIME      NOT NULL,
    ultimo_acesso   DATETIME
);

CREATE TABLE selecoes (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome         VARCHAR(80) NOT NULL,
    codigo_fifa  VARCHAR(3)  NOT NULL UNIQUE,
    bandeira_url VARCHAR(255),
    grupo        VARCHAR(2)
);

CREATE TABLE partidas (
    id                    BIGINT AUTO_INCREMENT PRIMARY KEY,
    selecao_mandante_id   BIGINT      NOT NULL,
    selecao_visitante_id  BIGINT      NOT NULL,
    data_hora             DATETIME    NOT NULL,
    fase                  VARCHAR(20) NOT NULL,        -- GRUPOS|OITAVAS|QUARTAS|SEMI|FINAL
    estadio               VARCHAR(120),
    grupo                 VARCHAR(2),
    status                VARCHAR(20) NOT NULL DEFAULT 'AGENDADA', -- AGENDADA|EM_ANDAMENTO|ENCERRADA
    gols_mandante         INT,
    gols_visitante        INT,
    CONSTRAINT fk_partida_mandante  FOREIGN KEY (selecao_mandante_id)  REFERENCES selecoes (id),
    CONSTRAINT fk_partida_visitante FOREIGN KEY (selecao_visitante_id) REFERENCES selecoes (id)
);

CREATE TABLE palpites (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id        BIGINT      NOT NULL,
    partida_id        BIGINT      NOT NULL,
    gols_mandante     INT         NOT NULL,
    gols_visitante    INT         NOT NULL,
    pontos_obtidos    INT,
    criterio_aplicado VARCHAR(20),                     -- PLACAR_EXATO|VENCEDOR|NENHUM
    criado_em         DATETIME    NOT NULL,
    atualizado_em     DATETIME,
    CONSTRAINT uk_palpite_usuario_partida UNIQUE (usuario_id, partida_id),
    CONSTRAINT fk_palpite_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios (id),
    CONSTRAINT fk_palpite_partida FOREIGN KEY (partida_id) REFERENCES partidas (id)
);

-- indices uteis para o ranking e consultas frequentes
CREATE INDEX idx_usuarios_ranking ON usuarios (pontuacao_total DESC, placares_exatos DESC, criado_em ASC);
CREATE INDEX idx_partidas_data    ON partidas (data_hora);
