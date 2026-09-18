package school.sptech;

import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Bucket;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;

public class Main {
    public static void main(String[] args) {

        String bucketName = "amazn-s3-vilefort";

        AwsSessionCredentials credentials = AwsSessionCredentials.create(
                Credentials.getAccessKeyId(),
                Credentials.getSecretAccessKey(),
                Credentials.getSessionToken());

        StaticCredentialsProvider credentialsProvider = StaticCredentialsProvider.create(credentials);

        try (S3Client s3Client = S3Client.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(credentialsProvider)
                .build()) {
            System.out.printf("""
                    Conexão realiada com sucesso
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
                    System.out.println("Arquivo %s | Tamanho: %s bytes".formatted(s3Object.key(), s3Object.size()));
                });
            }
        } catch (Exception e) {
            System.err.println("Erro na AWS" + e.getMessage());
        }
    }
}