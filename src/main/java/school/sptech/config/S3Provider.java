package school.sptech.config;

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.services.s3.S3Client;

public class S3Provider {
    public static S3Client createClient() {
        return S3Client.builder()
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }
}