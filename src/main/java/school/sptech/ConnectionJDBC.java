package school.sptech;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionJDBC {
    private static final String url = "jdbc:mysql://localhost:3306/roadvizor";
    private static final String user = "root";
    private static final String password = "Rezende11.";

    private static Connection connection;

    private static Connection getConnection(){
        try{
            if (connection == null){
               return connection = DriverManager.getConnection(url,user,password);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
