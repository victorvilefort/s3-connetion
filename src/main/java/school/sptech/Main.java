package school.sptech;

import school.sptech.config.S3Provider;
import school.sptech.service.Reader;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

public class Main {

    private static final String BUCKET_NAME = "datalake-gold-roadvizor-sptech";

    private static final String PREFIXO = "";

    private static final Pattern PADRAO_NOME =
            Pattern.compile("dados_\\d{8}_\\d{6}_\\d+\\.json$", Pattern.CASE_INSENSITIVE);

    public static void main(String[] args) {

        try (S3Client s3Client = S3Provider.createClient()) {

            System.out.printf("""
                    
                    ╔══════════════════════════════════════════════╗
                    ║              CONEXÃO COM AMAZON S3          ║
                    ╠══════════════════════════════════════════════╣
                    ║  ✓ Conexão realizada com sucesso!          ║
                    ║                                              ║
                    ║  Bucket: %-34s ║
                    ╚══════════════════════════════════════════════╝
                    
                    """, BUCKET_NAME);

            ListObjectsV2Request request = ListObjectsV2Request.builder()
                    .bucket(BUCKET_NAME)
                    .prefix(PREFIXO)
                    .build();

            List<S3Object> objetos = s3Client.listObjectsV2Paginator(request)
                    .contents()
                    .stream()
                    .filter(o -> !o.key().endsWith("/"))
                    .toList();

            if (objetos.isEmpty()) {

                System.out.println("""
                        
                        ┌──────────────────────────────────────────┐
                        │              BUCKET VAZIO                │
                        └──────────────────────────────────────────┘
                        """);
                return;
            }

            System.out.println("""
                    
                    ┌──────────────────────────────────────────┐
                    │              ARQUIVOS                    │
                    ├──────────────────────────────────────────┤
                    """);

            objetos.forEach(s3Object -> System.out.printf(
                    "                        %-45s %10d bytes%n",
                    s3Object.key(),
                    s3Object.size()
            ));

            System.out.println("""
                    └──────────────────────────────────────────┘
                    """);

            Optional<S3Object> maisRecente = objetos.stream()
                    .filter(o -> PADRAO_NOME.matcher(o.key()).find())
                    .max(Comparator.comparing(S3Object::key));

            if (maisRecente.isEmpty()) {
                System.out.println("Nenhum arquivo no padrão dados_AAAAMMDD_HHMMSS_N.json encontrado.");
                return;
            }

            String chave = maisRecente.get().key();

            System.out.printf("""
                    
                    ╔══════════════════════════════════════════════╗
                    ║          INICIANDO LEITURA DO ARQUIVO       ║
                    ╚══════════════════════════════════════════════╝
                    
                    Arquivo mais recente: %s
                    
                    """, chave);

            Reader reader = new Reader();
            reader.readContent(s3Client, BUCKET_NAME, chave);

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