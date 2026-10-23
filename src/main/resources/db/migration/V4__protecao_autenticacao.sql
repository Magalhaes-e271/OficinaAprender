CREATE TABLE auth_limite (
    chave VARCHAR(64) PRIMARY KEY,
    inicio TIMESTAMPTZ NOT NULL,
    tentativas INTEGER NOT NULL
);
CREATE INDEX idx_auth_limite_inicio ON auth_limite(inicio);
CREATE TABLE auth_mfa (
    usuario_id BIGINT PRIMARY KEY REFERENCES usuario(id) ON DELETE CASCADE,
    ativo BOOLEAN NOT NULL DEFAULT FALSE,
    segredo TEXT NOT NULL,
    expira_em TIMESTAMPTZ,
    ultimo_passo BIGINT NOT NULL DEFAULT -1
);
CREATE TABLE auth_mfa_recuperacao (
    usuario_id BIGINT NOT NULL REFERENCES auth_mfa(usuario_id) ON DELETE CASCADE,
    codigo_hash VARCHAR(64) NOT NULL,
    PRIMARY KEY (usuario_id, codigo_hash)
);
