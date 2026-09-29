package school.sptech.repository;

import school.sptech.config.ConnectionJDBC;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class JiraRepository extends ConnectionJDBC {

    public record JiraConfig(String domainJira, String emailJira, String apiTokenJira, String projectKey) {}

    public JiraConfig findByMac(String MacJSON){
        String sql = """
                SELECT 
                    e.domain_jira,
                    e.email_jira,
                    e.api_token_jira,
                    e.project_key
                FROM servidor s
                JOIN empresa e ON s.empresa_id = e.id_empresa
                WHERE s.endereco_mac = ?
                """;

        try(Connection connection = getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            if (connection == null){
                System.out.println("Não foi possível se conectar com o Banco de dados");
                return null;
            }

            preparedStatement.setString(1, MacJSON);

            try(ResultSet set = preparedStatement.executeQuery()){
                if(set.next()){
                    return new JiraConfig(
                            set.getString("domain_jira"),
                            set.getString("email_jira"),
                            set.getString("api_token_jira"),
                            set.getString("project_key"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}