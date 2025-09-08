DROP TABLE if EXISTS calculo;

CREATE TABLE calculo (
                         id serial NOT NULL PRIMARY KEY,
                         pet_id INT NOT NULL,
                         racao_id INT,
                         data_calculo TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         peso_atual DECIMAL(8,2) NOT NULL,
                         idade INT NOT NULL,
                         fase_vida VARCHAR(20) NOT NULL,
                         nivel_atv VARCHAR(50),
                         coef_min DECIMAL(8,2),
                         coef_max DECIMAL(8,2),
                         nem_media DECIMAL(10,2),
                         nem_min DECIMAL(10,2),
                         nem_max DECIMAL(10,2),
                         em DECIMAL(8,2) NOT NULL,
                         resultado DECIMAL(10,2),
                         resultado_min DECIMAL(10,2),
                         resultado_max DECIMAL(10,2),
                         FOREIGN KEY (pet_id) REFERENCES pet(id),
                         FOREIGN KEY (racao_id) REFERENCES tipo_racao(id)
);

