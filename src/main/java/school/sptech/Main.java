package school.sptech;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;

public class Main {
    public static void main(String[] args) {

        String bucketName = "amazn-s3-vilefort";

        try (S3Client s3Client = S3Provider.createClient()){
            System.out.printf("""
                    Conexão realizada com sucesso!
                    ---------------------------------
                    Listando arquivos do bucket: %s
                    %n""", bucketName);

            ListObjectsV2Request request = ListObjectsV2Request.builder()
                    .bucket(bucketName)
                    .build();

            ListObjectsV2Response response = s3Client.listObjectsV2(request);

            if (response.contents().isEmpty()) {
                System.out.println("Bucket Vazio");
            } else {
                response.contents().forEach(s3Object -> {
                    System.out.println("""
                            ------------------------------
                            Arquivo %s | Tamanho: %s bytes
                            ------------------------------
                            """.formatted(s3Object.key(), s3Object.size()));
                });

                System.out.println("Iniciando a leitura do arquivo...");

                Reader reader = new Reader();

                reader.readCSV(s3Client, bucketName, "victor.csv");
            }

        } catch (Exception e) {
            System.err.println("Erro na AWS" + e.getMessage());
        }
    }
}