--refatorando tabela, pois a primeira ideia era só para caes agora adicionando para gatos tambem
-- Adiciona a coluna 'especie' à tabela 'tipo_racao'
ALTER TABLE tipo_racao ADD COLUMN especie VARCHAR(20);

-- Atualiza todos os registros existentes para a espécie 'CACHORRO',
-- já que os valores atuais são para cães.
UPDATE tipo_racao SET especie = 'CACHORRO';

-- Torna a coluna 'especie' obrigatória (NOT NULL) agora que já a preenchemos.
ALTER TABLE tipo_racao ALTER COLUMN especie SET NOT NULL;

-- Adiciona uma restrição para garantir que não haverá duas entradas
-- com o mesmo tipo e mesma espécie (ex: duas "Premium" para "CACHORRO").
ALTER TABLE tipo_racao ADD CONSTRAINT uk_tipo_racao_tipo_especie UNIQUE (tipo, especie);
