DROP TABLE if EXISTS tipo_racao;

CREATE TABLE tipo_racao (
                            id serial NOT NULL primary key,
                            tipo VARCHAR(50) NOT NULL,
                            em DECIMAL(8,2) NOT NULL
);
