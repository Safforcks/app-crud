create table produtos(
    id                integer            auto_increment primary key,
    nome              varchar(150)       not null,
    preco             double             not null,
    quantidade        integer            not null,
    dataCadastro      datetime           default now()
);