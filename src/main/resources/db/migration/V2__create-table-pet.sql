DROP TABLE if EXISTS pet;

CREATE TABLE pet (
                     id serial NOT NULL primary key,
                     uuid UUID DEFAULT gen_random_uuid(),
                     tutor_id INT NOT NULL,
                     nome VARCHAR(100) NOT NULL,
                     especie VARCHAR(10) NOT NULL,
                     porte VARCHAR(20),
                     data_nasc DATE,
                     FOREIGN KEY (tutor_id) REFERENCES tutor(id)
);
