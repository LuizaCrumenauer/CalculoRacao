CREATE TABLE item_saude (
                            id SERIAL PRIMARY KEY,
                            nome VARCHAR(100) NOT NULL,
                            tipo VARCHAR(20) NOT NULL CHECK (tipo IN ('VACINA', 'VERMIFUGO', 'REMEDIO', 'OUTRO')),
                            tutor_id INT, -- NULO para itens globais (Admin), preenchido para itens de usuário
                            UNIQUE (nome, tutor_id),
                            FOREIGN KEY (tutor_id) REFERENCES tutor (id) ON DELETE CASCADE
);

CREATE TABLE registro_saude (
                                id SERIAL PRIMARY KEY,
                                pet_id INT NOT NULL,
                                item_saude_id INT NOT NULL,
                                data_aplicacao DATE NOT NULL,
                                proxima_dose DATE,
                                FOREIGN KEY (pet_id) REFERENCES pet (id) ON DELETE CASCADE,
                                FOREIGN KEY (item_saude_id) REFERENCES item_saude (id)
);
