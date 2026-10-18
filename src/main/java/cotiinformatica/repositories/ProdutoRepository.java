package cotiinformatica.repositories;

import cotiinformatica.entities.Produto;
import cotiinformatica.factories.ConnectionFactory;

public class ProdutoRepository {

    public void inserir(Produto produto) throws Exception {

        var factory = new ConnectionFactory();

        try (var connection = factory.obterConexao()){

            var statement = connection.prepareStatement("insert into produtos(nome, preco, quantidades) values(?, ?, ?)");
        }
    }
}
