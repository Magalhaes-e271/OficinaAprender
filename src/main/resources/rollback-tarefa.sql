-- Reversão restrita às alterações e aos dados fictícios da tarefa de 19/10.
-- Uma única transação: qualquer divergência interrompe sem apagar dados.
DO $reversao$
DECLARE
    seq RECORD;
    nome_seq TEXT;
BEGIN
    IF to_regclass('public.flyway_schema_history') IS NULL THEN
        RAISE NOTICE 'REVERSAO_TAREFA_JA_APLICADA';
        RETURN;
    END IF;
    PERFORM set_config('lock_timeout', '20s', true);
    LOCK TABLE usuario, perfil_usuario, relacao_usuario, meta, postagem, anexo,
        arquivo, compromisso, meta_dependencia, ativacao_conta IN ACCESS EXCLUSIVE MODE;

    IF NOT EXISTS (SELECT 1 FROM flyway_schema_history WHERE version = '2' AND success)
        OR (SELECT count(*) FROM usuario) <> 9
        OR (SELECT count(*) FROM usuario WHERE id IN (1,2,5,6,7,8)) <> 6
        OR (SELECT count(*) FROM usuario WHERE id IN (9,10,11) AND email LIKE 'validacao-%@example.test') <> 3
        OR (SELECT count(*) FROM usuario WHERE id IN (9,10) AND tipo_usuario = 'PROFISSIONAL' AND criado_por_id IS NULL) <> 2
        OR (SELECT count(*) FROM usuario WHERE id = 11 AND tipo_usuario = 'PACIENTE' AND criado_por_id = 9) <> 1
        OR EXISTS (SELECT 1 FROM usuario WHERE id IN (1,2,5,6,7,8) AND (NOT ativo OR NOT acesso_habilitado OR versao_token <> 0 OR criado_por_id IS NOT NULL))
        OR EXISTS (SELECT 1 FROM perfil_usuario WHERE id_usuario IN (1,2,5,6,7,8) AND foto_id IS NOT NULL)
        OR (SELECT count(*) FROM perfil_usuario WHERE id_usuario IN (1,2,5,6,7,8)) <> 6
        OR (SELECT count(*) FROM perfil_usuario) <> 9
        OR EXISTS (SELECT 1 FROM meta) OR EXISTS (SELECT 1 FROM postagem) OR EXISTS (SELECT 1 FROM anexo)
        OR EXISTS (SELECT 1 FROM compromisso) OR EXISTS (SELECT 1 FROM meta_dependencia) OR EXISTS (SELECT 1 FROM ativacao_conta)
        OR (SELECT count(*) FROM relacao_usuario) <> 1
        OR (SELECT count(*) FROM relacao_usuario WHERE id = 1 AND id_usuario_solicitante = 9 AND id_usuario_destinatario = 11) <> 1
        OR (SELECT count(*) FROM arquivo) <> 2
        OR EXISTS (SELECT 1 FROM arquivo WHERE id NOT IN (SELECT foto_id FROM perfil_usuario WHERE id_usuario IN (9,10) AND foto_id IS NOT NULL))
    THEN
        RAISE EXCEPTION 'Reversão interrompida: o estado não corresponde exclusivamente aos dados fictícios identificados.';
    END IF;

    DROP TABLE ativacao_conta;
    DROP TABLE compromisso;
    DROP TABLE meta_dependencia;
    DROP TABLE app_segredo;
    DELETE FROM relacao_usuario WHERE id = 1 AND id_usuario_solicitante = 9 AND id_usuario_destinatario = 11;
    ALTER TABLE anexo DROP COLUMN arquivo_id;
    ALTER TABLE postagem DROP COLUMN autor_id, DROP COLUMN responde_a_id;
    ALTER TABLE meta DROP COLUMN paciente_id, DROP COLUMN responsavel_id;
    ALTER TABLE relacao_usuario DROP COLUMN convidado_por_id;
    ALTER TABLE perfil_usuario DROP COLUMN foto_id;
    ALTER TABLE usuario DROP COLUMN criado_por_id, DROP COLUMN ativo, DROP COLUMN acesso_habilitado, DROP COLUMN versao_token;
    DELETE FROM perfil_usuario WHERE id_usuario IN (9,10,11);
    DELETE FROM usuario WHERE id IN (9,10,11);
    DROP TABLE arquivo;
    ALTER TABLE perfil_usuario ALTER COLUMN condicao TYPE VARCHAR(255);
    DROP INDEX IF EXISTS idx_meta_paciente;
    DROP INDEX IF EXISTS idx_postagem_meta;
    DROP INDEX IF EXISTS idx_anexo_postagem;
    DROP INDEX IF EXISTS idx_relacao_solicitante;
    DROP INDEX IF EXISTS idx_relacao_destinatario;
    DROP INDEX IF EXISTS uk_relacao_participantes;
    DROP TABLE flyway_schema_history;

    FOR seq IN SELECT * FROM (VALUES ('usuario',9),('relacao_usuario',1),('meta',1),('postagem',1),('anexo',1)) AS s(tabela,proximo)
    LOOP
        nome_seq := pg_get_serial_sequence(seq.tabela,'id');
        IF nome_seq IS NOT NULL THEN
            EXECUTE format('ALTER SEQUENCE %s RESTART WITH %s',nome_seq,seq.proximo);
        END IF;
    END LOOP;
    RAISE NOTICE 'REVERSAO_TAREFA_APLICADA';
END
$reversao$;
