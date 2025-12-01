ALTER TABLE item_saude DROP CONSTRAINT item_saude_tipo_check;

ALTER TABLE item_saude
    ADD CONSTRAINT item_saude_tipo_check
        CHECK (tipo IN ('VACINA', 'VERMIFUGO', 'ANTIPULGA', 'REMEDIO', 'OUTRO'));
