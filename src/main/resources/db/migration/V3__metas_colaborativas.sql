-- Preserva autoria e permite detectar alterações simultâneas.
ALTER TABLE meta ADD COLUMN versao BIGINT NOT NULL DEFAULT 0;
ALTER TABLE meta ADD COLUMN atualizado_por_id BIGINT REFERENCES usuario(id);
UPDATE meta SET atualizado_por_id = responsavel_id WHERE atualizado_por_id IS NULL;
