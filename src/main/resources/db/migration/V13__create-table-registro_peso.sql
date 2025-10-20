CREATE TABLE registro_peso (
                               id SERIAL PRIMARY KEY,
                               pet_id INT NOT NULL,
                               peso DECIMAL(8,2) NOT NULL,
                               data_registro DATE NOT NULL DEFAULT CURRENT_DATE,
                               FOREIGN KEY (pet_id) REFERENCES pet (id) ON DELETE CASCADE
);
