CREATE TABLE nutricao_caes_filhotes (
                                        id serial primary key,
                                        porte_adulto VARCHAR(50) NOT NULL,
                                        idade_meses_min INT,
                                        idade_meses_max INT,
                                        fator_correcao DECIMAL(4, 2)
);
