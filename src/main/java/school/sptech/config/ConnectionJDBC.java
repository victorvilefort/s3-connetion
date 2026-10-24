package school.sptech.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionJDBC {

    private static final String URL = System.getenv().getOrDefault(
            "DB_URL", "jdbc:mysql://localhost:3306/roadvizor");
    private static final String USER = exigir("DB_USER");
    private static final String PASSWORD = exigir("DB_PASSWORD");

    private static Connection connection;

    private static String exigir(String nome) {
        String valor = System.getenv(nome);
        if (valor == null || valor.isBlank()) {
            throw new IllegalStateException("Variável de ambiente obrigatória não definida: " + nome);
        }
        return valor;
    }

    protected static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            }
            return connection;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}