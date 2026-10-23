ALTER TABLE usuario
    ADD COLUMN IF NOT EXISTS ativo BOOLEAN NOT NULL DEFAULT TRUE;
ALTER TABLE usuario
    ADD COLUMN IF NOT EXISTS acesso_habilitado BOOLEAN NOT NULL DEFAULT TRUE;
ALTER TABLE usuario
    ADD COLUMN IF NOT EXISTS versao_token INTEGER NOT NULL DEFAULT 0;
ALTER TABLE usuario
    ADD COLUMN IF NOT EXISTS criado_por_id BIGINT REFERENCES usuario(id);
CREATE TABLE arquivo
(
    id           BIGSERIAL PRIMARY KEY,
    nome         VARCHAR(255),
    tipo         VARCHAR(255),
    tamanho      BIGINT NOT NULL,
    data_criacao TIMESTAMPTZ(6),
    conteudo     BYTEA  NOT NULL
);
ALTER TABLE perfil_usuario
    ADD COLUMN IF NOT EXISTS foto_id BIGINT REFERENCES arquivo(id);
ALTER TABLE perfil_usuario ALTER COLUMN condicao TYPE VARCHAR(2000);
ALTER TABLE relacao_usuario
    ADD COLUMN IF NOT EXISTS convidado_por_id BIGINT REFERENCES usuario(id);
ALTER TABLE meta
    ADD COLUMN IF NOT EXISTS paciente_id BIGINT REFERENCES usuario(id);
ALTER TABLE meta
    ADD COLUMN IF NOT EXISTS responsavel_id BIGINT REFERENCES usuario(id);
-- Preserva a associação dos registros antigos a partir do vínculo original.
UPDATE meta m
SET paciente_id    = CASE WHEN s.tipo_usuario = 'PACIENTE' THEN s.id ELSE d.id END,
    responsavel_id = CASE WHEN s.tipo_usuario = 'PROFISSIONAL' THEN s.id ELSE d.id END FROM relacao_usuario r JOIN usuario s
ON s.id = r.id_usuario_solicitante
    JOIN usuario d ON d.id = r.id_usuario_destinatario
WHERE m.id_relacao_usuario = r.id
  AND r.tipo_relacao = 'PROFISSIONAL_PACIENTE'
  AND m.paciente_id IS NULL;
UPDATE meta
SET id_relacao_usuario = NULL
WHERE paciente_id IS NOT NULL;
CREATE TABLE meta_dependencia
(
    meta_id     BIGINT NOT NULL REFERENCES meta (id) ON DELETE CASCADE,
    anterior_id BIGINT NOT NULL REFERENCES meta (id),
    PRIMARY KEY (meta_id, anterior_id),
    CHECK (meta_id <> anterior_id)
);
ALTER TABLE postagem
    ADD COLUMN IF NOT EXISTS autor_id BIGINT REFERENCES usuario(id);
ALTER TABLE postagem
    ADD COLUMN IF NOT EXISTS responde_a_id BIGINT REFERENCES postagem(id);
ALTER TABLE anexo
    ADD COLUMN IF NOT EXISTS arquivo_id BIGINT REFERENCES arquivo(id);
CREATE TABLE compromisso
(
    id              BIGSERIAL PRIMARY KEY,
    profissional_id BIGINT NOT NULL REFERENCES usuario (id),
    paciente_id     BIGINT REFERENCES usuario (id),
    titulo          VARCHAR(255),
    inicio          TIMESTAMPTZ(6),
    fim             TIMESTAMPTZ(6),
    observacoes     TEXT,
    CHECK (fim > inicio)
);
CREATE INDEX idx_agenda_profissional_intervalo ON compromisso (profissional_id, inicio, fim);
CREATE INDEX idx_agenda_paciente_intervalo ON compromisso (paciente_id, inicio, fim);
CREATE INDEX idx_meta_paciente ON meta (paciente_id);
CREATE INDEX idx_postagem_meta ON postagem (id_meta);
CREATE INDEX idx_anexo_postagem ON anexo (id_postagem);
CREATE INDEX idx_relacao_solicitante ON relacao_usuario (id_usuario_solicitante);
CREATE INDEX idx_relacao_destinatario ON relacao_usuario (id_usuario_destinatario);
-- Se houver duplicatas antigas, a migração pausa sem apagar dados para saneamento e revisão.
CREATE UNIQUE INDEX uk_relacao_participantes ON relacao_usuario
    (LEAST(id_usuario_solicitante, id_usuario_destinatario), GREATEST(id_usuario_solicitante, id_usuario_destinatario));
CREATE TABLE app_segredo
(
    nome  VARCHAR(100) PRIMARY KEY,
    valor VARCHAR(256) NOT NULL
);
CREATE TABLE ativacao_conta
(
    token_hash VARCHAR(64) PRIMARY KEY,
    usuario_id BIGINT       NOT NULL UNIQUE REFERENCES usuario (id),
    expira_em  TIMESTAMP(6) NOT NULL
);
