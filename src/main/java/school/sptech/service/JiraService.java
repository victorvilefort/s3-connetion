package school.sptech.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import school.sptech.dto.IssueFieldsDTO;
import school.sptech.dto.IssueRequestDTO;
import school.sptech.dto.IssueTypeDTO;
import school.sptech.dto.ProjectDTO;
import school.sptech.repository.JiraRepository.JiraConfig;

public class JiraService {

    private final String baseUrl;
    private final String authHeader;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public JiraService(String baseUrl, String email, String apiToken) {
        this.baseUrl = baseUrl;

        String auth = email + ":" + apiToken;
        this.authHeader = "Basic " + Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));

        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .build();

        this.objectMapper = new ObjectMapper();
    }

    public JiraService(JiraConfig config) {
        String dominio = config.domainJira();
        this.baseUrl = dominio.startsWith("http") ? dominio : "https://" + dominio;

        String auth = config.emailJira() + ":" + config.apiTokenJira();
        this.authHeader = "Basic " + java.util.Base64.getEncoder().encodeToString(auth.getBytes(java.nio.charset.StandardCharsets.UTF_8));

        this.httpClient = java.net.http.HttpClient.newBuilder()
                .connectTimeout(java.time.Duration.ofSeconds(30))
                .build();

        this.objectMapper = new ObjectMapper();
    }

    public String createIssue(String projectKey, String summary, String comments, String issueType) throws IOException, InterruptedException {
        // Monta o corpo da requisição usando os DTOs
        ProjectDTO project = new ProjectDTO(projectKey);
        IssueTypeDTO type = new IssueTypeDTO(issueType);
        IssueFieldsDTO fields = new IssueFieldsDTO(project, summary, comments, type);
        IssueRequestDTO issueRequest = new IssueRequestDTO(fields);

        // Transforma o objeto Java em JSON
        String json = objectMapper.writeValueAsString(issueRequest);

        // Cria a requisição POST para o endpoint de criação de issues
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/rest/api/3/issue"))
                .timeout(Duration.ofSeconds(120))
                .header("Authorization", authHeader)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        // Envia a requisição
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        int status = response.statusCode();

        if (status >= 200 && status < 300) {
            return response.body();
        }

        throw new RuntimeException("Jira request failed: " + status + " - " + response.body());
    }
}