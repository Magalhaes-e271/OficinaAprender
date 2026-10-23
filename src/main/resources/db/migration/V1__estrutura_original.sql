-- Compatível com os bancos previamente criados por Hibernate ddl-auto=update.
-- Não apaga nem substitui registros existentes.
CREATE TABLE IF NOT EXISTS usuario
(
    id
    BIGSERIAL
    PRIMARY
    KEY,
    email
    VARCHAR
(
    255
) NOT NULL UNIQUE,
    senha VARCHAR
(
    255
) NOT NULL,
    tipo_usuario VARCHAR
(
    255
)
    );
CREATE TABLE IF NOT EXISTS perfil_usuario
(
    id_usuario
    BIGINT
    PRIMARY
    KEY
    REFERENCES
    usuario
(
    id
),
    nome VARCHAR
(
    255
), telefone VARCHAR
(
    255
), cpf VARCHAR
(
    255
),
    url_foto_perfil VARCHAR
(
    255
), foto_arquivo VARCHAR
(
    255
),
    data_nascimento DATE, ocupacao VARCHAR
(
    255
), instituicao VARCHAR
(
    255
), conteudo_html TEXT,
    data_criacao TIMESTAMP
(
    6
) NOT NULL, data_atualizacao TIMESTAMP
(
    6
) NOT NULL,
    horario_de_entrada TIME
(
    6
), horario_de_saida TIME
(
    6
), condicao VARCHAR
(
    255
)
    );
CREATE TABLE IF NOT EXISTS relacao_usuario
(
    id
    BIGSERIAL
    PRIMARY
    KEY,
    id_usuario_solicitante
    BIGINT
    REFERENCES
    usuario
(
    id
),
    id_usuario_destinatario BIGINT REFERENCES usuario
(
    id
),
    tipo_relacao VARCHAR
(
    255
), solicitacao SMALLINT,
    data_solicitacao TIMESTAMP
(
    6
), data_aceitacao TIMESTAMP
(
    6
)
    );
CREATE TABLE IF NOT EXISTS meta
(
    id
    BIGSERIAL
    PRIMARY
    KEY,
    id_relacao_usuario
    BIGINT
    REFERENCES
    relacao_usuario
(
    id
),
    area VARCHAR
(
    255
), titulo VARCHAR
(
    255
), conteudo_html TEXT,
    concluido BOOLEAN NOT NULL DEFAULT FALSE,
    data_criacao TIMESTAMP
(
    6
), data_atualizacao TIMESTAMP
(
    6
)
    );
CREATE TABLE IF NOT EXISTS postagem
(
    id
    BIGSERIAL
    PRIMARY
    KEY,
    id_meta
    BIGINT
    REFERENCES
    meta
(
    id
), titulo VARCHAR
(
    255
), conteudo_html TEXT,
    tipo_postagem VARCHAR
(
    255
), visualizacao VARCHAR
(
    255
),
    data_criacao TIMESTAMP
(
    6
), data_atualizacao TIMESTAMP
(
    6
)
    );
CREATE TABLE IF NOT EXISTS anexo
(
    id
    BIGSERIAL
    PRIMARY
    KEY,
    id_postagem
    BIGINT
    REFERENCES
    postagem
(
    id
),
    nome VARCHAR
(
    255
), url_arquivo VARCHAR
(
    255
), tamanho_arquivo BIGINT,
    tipo_arquivo VARCHAR
(
    255
), data_upload TIMESTAMP
(
    6
)
    );
