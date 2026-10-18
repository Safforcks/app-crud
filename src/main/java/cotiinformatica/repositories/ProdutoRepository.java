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

    public boolean atualizar(Produto produto) throws Exception {

        var factory = new ConnectionFactory();

        try (var connection = factory.obterConexao()){

            var statement = connection.prepareStatement("update produtos set nome=?, preco=?, quantidade=? where id=?");

            statement.setString(1, produto.getNome());
            statement.setDouble(2, produto.getPreco());
            statement.setInt(3, produto.getQuantidade());
            statement.setInt(4, produto.getId());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean excluir(Integer id) throws Exception {

        var factory = new ConnectionFactory();

        try (var connection = factory.obterConexao()) {

            var statement = connection.prepareStatement("delete from produtos where id=?");

            statement.setInt(1, id);
            return statement.executeUpdate() > 0;
        }
    }
}
