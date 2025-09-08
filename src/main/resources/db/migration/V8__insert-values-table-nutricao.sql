-- Cães 1-2 anos
INSERT INTO nutricao_caes_adultos (descricao, idade_min_anos, idade_max_anos, nivel_atv, coef_min, coef_max) VALUES
                                                                                                                 ('Cães jovens', 1, 2, 'Baixa atividade (<1h/dia)', 125.00, 130.00),
                                                                                                                 ('Cães jovens', 1, 2, 'Atividade moderada (1–3h/dia, baixo impacto)', 125.00, 140.00),
                                                                                                                 ('Cães jovens', 1, 2, 'Atividade moderada (1–3h/dia, alto impacto)', 130.00, 140.00),
                                                                                                                 ('Cães jovens', 1, 2, 'Atividade intensa (3–6h/dia)', 150.00, 175.00),
                                                                                                                 ('Cães jovens', 1, 2, 'Atividade intensa em condições extremas', 860.00, 1240.00),
                                                                                                                 ('Cães jovens', 1, 2, 'Tendência à obesidade', 125.00, 125.00);
-- Cães 3-7 anos
INSERT INTO nutricao_caes_adultos (descricao, idade_min_anos, idade_max_anos, nivel_atv, coef_min, coef_max) VALUES
                                                                                                                 ('Cães adultos', 3, 7, 'Baixa atividade (<1h/dia)', 95, 110),
                                                                                                                 ('Cães adultos', 3, 7, 'Atividade moderada (1–3h/dia, baixo impacto)', 110, 120),
                                                                                                                 ('Cães adultos', 3, 7, 'Atividade moderada (1–3h/dia, alto impacto)', 125, 130),
                                                                                                                 ('Cães adultos', 3, 7, 'Atividade intensa (3–6h/dia)', 150, 175),
                                                                                                                 ('Cães adultos', 3, 7, 'Atividade intensa em condições extremas', 860, 1240),
                                                                                                                 ('Cães adultos', 3, 7, 'Tendência à obesidade', 90, 95);

-- Cães >7 anos (seniores)
INSERT INTO nutricao_caes_adultos (descricao, idade_min_anos, idade_max_anos, nivel_atv, coef_min, coef_max) VALUES
                                                                                                                 ('Cães seniores', 8, 50, 'Baixa atividade (<1h/dia)', 95, 110),
                                                                                                                 ('Cães seniores', 8, 50, 'Atividade moderada (1–3h/dia, baixo impacto)', 95, 120),
                                                                                                                 ('Cães seniores', 8, 50, 'Atividade moderada (1–3h/dia, alto impacto)', 125, 125),
                                                                                                                 ('Cães seniores', 8, 50, 'Atividade intensa (3–6h/dia)', 150, 175),
                                                                                                                 ('Cães seniores', 8, 50, 'Atividade intensa em condições extremas', 860, 1240),
                                                                                                                 ('Cães seniores', 8, 50, 'Tendência à obesidade', 80, 90);
