package cotiinformatica.repositories;

import cotiinformatica.entities.Produto;
import cotiinformatica.factories.ConnectionFactory;

public class ProdutoRepository {

    public void inserir(Produto produto) throws Exception {

        var factory = new ConnectionFactory();

        try (var connection = factory.obterConexao()){

            var statement = connection.prepareStatement("insert into produtos(nome, preco, quantidade) values(?, ?, ?)");

            statement.setString(1, produto.getNome());
            statement.setDouble(2, produto.getPreco());
            statement.setInt(3, produto.getQuantidade());
            statement.execute();
        }
    }
}
