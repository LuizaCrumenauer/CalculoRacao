-- Gatos adultos
INSERT INTO nutricao_gatos (descricao, fase_vida, nivel_atv, coef_min, coef_max) VALUES
                                                                                     ('Castrados / baixa atividade', 'ADULTO', 'BAIXA', 52, 75),
                                                                                     ('Gatos ativos', 'ADULTO', 'ATIVO', 100, 100);

-- Gatos filhotes
INSERT INTO nutricao_gatos (descricao, fase_vida, nivel_atv, coef_min, coef_max) VALUES
                                                                                     ('Filhote <4 meses', 'FILHOTE', 'PADRAO', 2.0, 2.5),
                                                                                     ('Filhote 4-9 meses', 'FILHOTE', 'PADRAO', 1.75, 2.0),
                                                                                     ('Filhote 9-12 meses', 'FILHOTE', 'PADRAO', 1.5, 1.5);
