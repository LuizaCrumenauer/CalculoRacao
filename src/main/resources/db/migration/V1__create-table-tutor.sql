DROP TABLE if EXISTS tutor;

CREATE TABLE tutor (
                       id serial NOT NULL primary key,
                       uuid UUID DEFAULT gen_random_uuid(),
                       nome VARCHAR(100) NOT NULL,
                       cpf CHAR(11) NOT NULL unique,
                       email VARCHAR(100) NOT NULL unique,
                       telefone VARCHAR(20) NOT NULL,
                       cep VARCHAR(9) NOT NULL,
                       logradouro VARCHAR(200) NOT NULL,
                       numero VARCHAR(20),
                       complemento VARCHAR(100),
                       bairro VARCHAR(100) NOT NULL,
                       cidade VARCHAR(100) NOT NULL,
                       uf CHAR(2) NOT NULL

);
