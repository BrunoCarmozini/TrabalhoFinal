package model.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConectaBD {
    private Connection conexao;

    // construtor
    public ConectaBD() throws ClassNotFoundException {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
            String url = "jdbc:mariadb://69.49.241.46:3306/mikae731_wp676";
            String user = "mikae731_wp676";
            String pwd = "-JWvnoLn8FDB";
            conexao = DriverManager.getConnection(url, user, pwd);
            System.out.println("Conexão realizada com sucesso!");
        } catch (SQLException e) {
            System.out.println("Erro ao conectar ao banco de dados: " + e.getMessage());
        }
    }

    /**
     * Método que retorna a conexão com o banco de dados
     * 
     * @return um objeto do tipo Connection
     */
    public Connection getConexao() {
        return conexao;
    }

    // Exemplo de uso
    public static void main(String[] args) {
        try {
            ConectaBD conectaBD = new ConectaBD();
            Connection conexao = conectaBD.getConexao();
            // Faça o uso da conexão com o banco de dados aqui
        } catch (ClassNotFoundException e) {
            System.out.println("Driver do banco de dados não encontrado: " + e.getMessage());
        }
    }
}
