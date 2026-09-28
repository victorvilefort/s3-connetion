package school.sptech;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;

import static java.lang.Math.round;

public class Main {
    public static void main(String[] args) {

        String bucketName = "amazn-s3-vilefort";
        try (S3Client s3Client = S3Provider.createClient()) {
            System.out.printf("""
            
            ╔══════════════════════════════════════════════╗
            ║          CONEXÃO COM AMAZON S3              ║
            ╠══════════════════════════════════════════════╣
            ║  ✓ Conexão realizada com sucesso!          ║
            ║                                              ║
            ║  Bucket: %-34s ║
            ╚══════════════════════════════════════════════╝
            
            """, bucketName);

            System.out.println("Arquivos encontrados:");
            System.out.println("──────────────────────────────────────────────");

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
                            """.formatted(s3Object.key(), round(s3Object.size())));
                });

                System.out.println("Iniciando a leitura do arquivo...");

                Reader reader = new Reader();

                reader.readContent(s3Client, bucketName, "victor.csv");
            }

        } catch (Exception e) {
            System.err.println("Erro na AWS" + e.getMessage());
        }
    }
}