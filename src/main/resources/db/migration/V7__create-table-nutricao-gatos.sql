CREATE TABLE nutricao_gatos (
                                id serial primary key,
                                descricao VARCHAR(100),
                                fase_vida VARCHAR(20),
                                nivel_atv VARCHAR(50),
                                coef_min DECIMAL(8, 2),
                                coef_max DECIMAL(8, 2)
);

