package school.sptech.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import school.sptech.dto.ResponseDTO;
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

        try(ResponseInputStream<GetObjectResponse> inputStream = s3Client.getObject(getObjectRequest)){
            ObjectMapper objectMapper = new ObjectMapper();


            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            JavaTimeModule javaTimeModule = new JavaTimeModule();

            javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(formatter));
            objectMapper.registerModule(javaTimeModule);

            ResponseDTO responseDTO = objectMapper.readValue(inputStream, ResponseDTO.class);
            System.out.println("--- CONTEÚDO DO JSON CONSUMIDO DA GOLD ---");
            String jsonFormated = objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(responseDTO);
            System.out.println(jsonFormated);

            if (responseDTO.getMetricas_monitorizadas() != null) {
                System.out.println("Quantidade de métricas monitoradas: " + responseDTO.getMetricas_monitorizadas().size());
            }
            System.out.println("----------------------------------------");

        } catch (Exception e) {
            System.err.println("Erro ao processar arquivos do S3: " + e.getMessage());
            e.printStackTrace(); // Ajuda a ver a pilha de erro completa se algo falhar
        }
    }
}
