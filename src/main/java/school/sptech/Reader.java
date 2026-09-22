package school.sptech;

import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;


public class Reader {
    public void readCSV(S3Client s3Client, String bucketName, String fileName) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        System.out.println("Conectando ao S3 e baixando o fluxo do arquivo...");

        try (ResponseInputStream<GetObjectResponse> inputStream = s3Client.getObject(getObjectRequest);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

            String row;
            System.out.println("Conteudo do CSV:");

            while ((row = reader.readLine()) != null) {
                String[] colunas = row.split(";");

                if (colunas.length > 0) {
                    System.out.println("Dados da linha: " + colunas[2]);
                }
            }
            System.out.println("----------------------------------------");
        } catch (Exception e) {
            System.err.println("Erro ao processar arquivos do S3: " + e.getMessage());
        }
    }
}
