package school.sptech.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import school.sptech.dto.MetricasDTO;
import school.sptech.dto.ResponseDTO;
import school.sptech.repository.JiraRepository;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Reader {
    public void readContent(S3Client s3Client, String bucketName, String fileName) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        System.out.println("Conectando ao S3 e baixando o fluxo do arquivo...");

        try (ResponseInputStream<GetObjectResponse> inputStream = s3Client.getObject(getObjectRequest)) {
            ObjectMapper objectMapper = new ObjectMapper();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            JavaTimeModule javaTimeModule = new JavaTimeModule();

            javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(formatter));
            objectMapper.registerModule(javaTimeModule);

            ResponseDTO responseDTO = objectMapper.readValue(inputStream, ResponseDTO.class);

            String responseJSON = objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(responseDTO);

            System.out.println(responseJSON);

            Boolean dispararAlerta = false;
            String componente = "";
            Double valor = 0.0;
            String unidade = "";
            Double limiteMax = 0.0;

            if (responseDTO.getMetricas_monitorizadas() != null) {
                for (MetricasDTO metricasDTO : responseDTO.getMetricas_monitorizadas()) {
                    if (metricasDTO.getEm_alerta() != null && metricasDTO.getEm_alerta()) {
                        componente = metricasDTO.getComponente();
                        valor = metricasDTO.getValor_medido();
                        unidade = metricasDTO.getUnidade_medida();
                        limiteMax = metricasDTO.getLimite_max();
                        dispararAlerta = true;;
                        break;
                    }
                }
            }

            if (dispararAlerta) {
                System.out.println("Abrindo chamado no jira...");

                System.out.println("Buscando no banco pelo MAC enviado pelo JSON: [" + responseDTO.getEndereco_mac() + "]");

                JiraRepository repository = new JiraRepository();
                JiraRepository.JiraConfig config = repository.findByMac(responseDTO.getEndereco_mac());

                if (config != null) {
                    JiraService jiraService = new JiraService(config);

                    String tipoChamado = "10010";

                    String title = "🚨 ALERTA: Componente [%s] em nível crítico no servidor %s"
                            .formatted(componente, responseDTO.getApelido());

                    String description = """
                        
                        🖥️ SERVIDOR : %s (MAC: %s)
                        🏢 Empresa  : %s
                        📍 Local    : %s (%s) | 🕒 Horário: %s
                        -------------------------------------------------------------
                        📊 Componente: %s
                        📈 Valor     : %.2f %s (Limite: %.2f)
                        =============================================================
                        """.formatted(
                            responseDTO.getApelido(),
                            responseDTO.getEndereco_mac(),
                            responseDTO.getEmpresa(),
                            responseDTO.getLocalizacao_km(),
                            responseDTO.getSentido(),
                            responseDTO.getTimestamp(),
                            componente,
                            valor,
                            unidade != null ? unidade : "",
                            limiteMax
                    );

                    String responseJira = jiraService.createIssue(config.projectKey(), title, description, tipoChamado);
                    System.out.println("🚀 Chamado aberto com sucesso! Resposta do Jira:\n" + responseJira);
                } else {
                    System.out.println(" ❌ Alerta gerado, mas nenhuma credencial do Jira foi encontrada no Banco!");
                }
            } else {
                System.out.println(" ✅ Todas as métricas estão normais. Nenhuma ação necessária.");
            }

        } catch (Exception e) {
            System.err.println("Erro ao processar arquivos do S3: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
