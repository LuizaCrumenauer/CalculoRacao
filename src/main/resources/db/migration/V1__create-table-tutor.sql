DROP TABLE if EXISTS tutor;

CREATE TABLE tutor (
                       id serial NOT NULL primary key,
                       nome VARCHAR(100) NOT NULL,
                       email VARCHAR(100) NOT NULL,
                       telefone VARCHAR(20),
                       logradouro VARCHAR(200),
                       numero VARCHAR(20),
                       complemento VARCHAR(100),
                       bairro VARCHAR(100),
                       cidade VARCHAR(100),
                       uf CHAR(2),
                       cep VARCHAR(9)
);
