package school.sptech;


import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

public class S3Provider {
    public static S3Client createClient() {
        AwsSessionCredentials credentials = AwsSessionCredentials.create(
                Credentials.getAccessKeyId(),
                Credentials.getSecretAccessKey(),
                Credentials.getSessionToken()
        );

        StaticCredentialsProvider credentialsProvider = StaticCredentialsProvider.create(credentials);

        return S3Client.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(credentialsProvider)
                .build();
    }
}
