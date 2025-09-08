CREATE TABLE nutricao_caes_adultos (
                                       id serial primary key,
                                       descricao VARCHAR(100),
                                       idade_min_anos INT,
                                       idade_max_anos INT,
                                       nivel_atv VARCHAR(50),
                                       coef_min DECIMAL(8,2),
                                       coef_max DECIMAL(8,2)
);

