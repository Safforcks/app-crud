package cotiinformatica.factories;

import java.sql.Connection;
import java.sql.DriverManager;

// Cria conexões com o banco de dados da aplicação.

public class ConnectionFactory {
     // Abre e retorna uma conexão com o banco de dados MySQL.
     // @return conexão com o banco de dados
     //@throws Exception se não for possível estabelecer a conexão

    public Connection obterConexao() throws Exception {

        var host = "jdbc:mysql://localhost:3307/app-crud";
        var user = "coti";
        var pass = "coti";

        return DriverManager.getConnection(host, user, pass);
    }
}
