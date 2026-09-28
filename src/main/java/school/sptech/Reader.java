package school.sptech;

import com.fasterxml.jackson.databind.ObjectMapper;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

public class Reader {
    public void readContent(S3Client s3Client, String bucketName, String fileName) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        System.out.println("Conectando ao S3 e baixando o fluxo do arquivo...");

        try(ResponseInputStream<GetObjectResponse> inputStream = s3Client.getObject(getObjectRequest)){
            ObjectMapper objectMapper = new ObjectMapper();

            ResponseDTO responseDTO = objectMapper.readValue(inputStream, ResponseDTO.class);
            System.out.println("--- CONTEÚDO DO JSON CONSUMIDO DA GOLD ---");
            System.out.println("ID Servidor: " + responseDTO.getId_servidor());
            System.out.println("Apelido: " + responseDTO.getApelido());
            System.out.println("Empresa: " + responseDTO.getEmpresa());
            System.out.println("Data: " + responseDTO.getTimestamp());

            if (responseDTO.getMetricas_monitoradas() != null) {
                System.out.println("Quantidade de métricas monitoradas: " + responseDTO.getMetricas_monitoradas().size());
            }
            System.out.println("----------------------------------------");

        } catch (Exception e) {
            System.err.println("Erro ao processar arquivos do S3: " + e.getMessage());
            e.printStackTrace(); // Ajuda a ver a pilha de erro completa se algo falhar
        }
    }
}
