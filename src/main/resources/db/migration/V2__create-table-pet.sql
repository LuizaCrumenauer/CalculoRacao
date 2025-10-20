CREATE TABLE pet (
                     id serial NOT NULL primary key,
                     uuid UUID DEFAULT gen_random_uuid(),
                     tutor_id INT NOT NULL,
                     nome VARCHAR(100) NOT NULL,
                     especie VARCHAR(20) NOT NULL CHECK (especie IN ('GATO', 'CACHORRO')),
                     porte VARCHAR(20) NOT NULL CHECK (porte IN ('PEQUENO', 'MEDIO', 'GRANDE', 'GIGANTE')),
                     sexo VARCHAR(10) NOT NULL CHECK (sexo IN ('MACHO', 'FEMEA')),
                     data_nasc DATE,
                     FOREIGN KEY (tutor_id) REFERENCES tutor(id) ON DELETE CASCADE
);
