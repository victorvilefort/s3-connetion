package school.sptech;

import school.sptech.config.S3Provider;
import school.sptech.service.Reader;
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
                    ║              CONEXÃO COM AMAZON S3          ║
                    ╠══════════════════════════════════════════════╣
                    ║  ✓ Conexão realizada com sucesso!          ║
                    ║                                              ║
                    ║  Bucket: %-34s ║
                    ╚══════════════════════════════════════════════╝
                    
                    """, bucketName);

            ListObjectsV2Request request = ListObjectsV2Request.builder()
                    .bucket(bucketName)
                    .build();

            ListObjectsV2Response response = s3Client.listObjectsV2(request);

            if (response.contents().isEmpty()) {

                System.out.println("""
                        
                        ┌──────────────────────────────────────────┐
                        │              BUCKET VAZIO                │
                        └──────────────────────────────────────────┘
                        """);

            } else {

                System.out.println("""
                        
                        ┌──────────────────────────────────────────┐
                        │              ARQUIVOS                    │
                        ├──────────────────────────────────────────┤
                        """);

                response.contents().forEach(s3Object -> {
                    System.out.printf(
                            "                        %-25s %8d bytes%n",
                            s3Object.key(),
                            round(s3Object.size())
                    );
                });

                System.out.println("""
                        └──────────────────────────────────────────┘
                        """);

                System.out.println("""
                        
                        ╔══════════════════════════════════════════════╗
                        ║          INICIANDO LEITURA DO ARQUIVO       ║
                        ╚══════════════════════════════════════════════╝
                        """);

                Reader reader = new Reader();

                reader.readContent(
                        s3Client,
                        bucketName,
                        "teste-servidor.json"
                );
            }

        } catch (Exception e) {
            System.err.println("""
                    
                    ╔══════════════════════════════════════════════╗
                    ║                  ERRO AWS                  ║
                    ╠══════════════════════════════════════════════╣
                    ║  %s
                    ╚══════════════════════════════════════════════╝
                    """.formatted(e.getMessage()));
        }
    }
}