# S3 Connection · Roadvizor

Aplicação Java que lê o arquivo de métricas mais recente do data lake (camada **gold**) no **Amazon S3**, verifica se algum componente monitorado está em alerta e, se estiver, **abre automaticamente um chamado no Jira** da empresa dona do servidor.

Faz parte do projeto **Roadvizor** (SPTech).

---

## Como funciona

```
┌────────────┐   1. lista objetos    ┌──────────────────────────┐
│            │ ────────────────────► │  S3: datalake-gold-...   │
│    Main    │ ◄──────────────────── │  dados_AAAAMMDD_..._N.json│
│            │   2. arquivo + recente└──────────────────────────┘
└─────┬──────┘
      │ 3. lê e interpreta o JSON
      ▼
┌────────────┐  alerta?  ┌────────────────┐  4. busca credenciais   ┌─────────┐
│   Reader   │ ────────► │ JiraRepository │ ──────────────────────► │  MySQL  │
└─────┬──────┘    sim    └────────────────┘   pelo MAC do servidor  └─────────┘
      │ 5. cria chamado
      ▼
┌────────────┐   POST /rest/api/3/issue   ┌──────────┐
│JiraService │ ─────────────────────────► │   Jira   │
└────────────┘                            └──────────┘
```

1. `Main` conecta ao S3 e lista todos os arquivos do bucket.
2. Entre eles, filtra os que seguem o padrão `dados_AAAAMMDD_HHMMSS_N.json` e escolhe o **mais recente** (maior nome/chave).
3. `Reader` baixa o arquivo e converte o JSON em objetos Java.
4. Se alguma métrica tiver `em_alerta = true`, o `JiraRepository` busca no MySQL as credenciais do Jira da empresa a que o servidor (identificado pelo MAC) pertence.
5. O `JiraService` abre o chamado via API REST v3 do Jira, com um resumo e uma descrição do problema.

Se nenhuma métrica estiver em alerta, nada é feito.

---

## Tecnologias

- Java 21
- Maven
- AWS SDK for Java v2 (S3)
- Jackson (JSON e datas)
- MySQL Connector/J
- `java.net.http.HttpClient` (integração com o Jira)

---

## Estrutura do projeto

```
src/main/java/school/sptech/
├── Main.java                  # Ponto de entrada: lista o S3 e escolhe o arquivo mais recente
├── config/
│   ├── S3Provider.java        # Cria o cliente S3 (credenciais padrão da AWS)
│   └── ConnectionJDBC.java    # Conexão com o MySQL via variáveis de ambiente
├── dto/                       # Modelos do JSON do S3 e do payload do Jira
├── repository/
│   └── JiraRepository.java    # Busca as credenciais do Jira pelo MAC do servidor
└── service/
    ├── Reader.java            # Lê o JSON, detecta alertas e dispara o chamado
    └── JiraService.java       # Cliente HTTP da API do Jira
```

---

## Pré-requisitos

- **JDK 21** e **Maven** instalados
- Credenciais **AWS** com permissão de leitura no bucket (`s3:ListBucket` e `s3:GetObject`)
- Um banco **MySQL** com as tabelas `servidor` e `empresa` (veja [Banco de dados](#banco-de-dados))
- Uma conta **Jira Cloud** com token de API

---

## Configuração

### Variáveis de ambiente

| Variável         | Obrigatória | Descrição                                                   | Padrão                                  |
| ---------------- | :---------: | ----------------------------------------------------------- | --------------------------------------- |
| `DB_USER`        |     Sim     | Usuário do MySQL                                            | —                                       |
| `DB_PASSWORD`    |     Sim     | Senha do MySQL                                              | —                                       |
| `DB_URL`         |     Não     | URL JDBC do banco                                           | `jdbc:mysql://localhost:3306/roadvizor` |
| `AWS_REGION`     |     Sim*    | Região do bucket (ex.: `us-east-1`)                         | —                                       |

\* Necessária caso a região não esteja definida no seu perfil da AWS.

A aplicação não inicia se `DB_USER` ou `DB_PASSWORD` não estiverem definidas.

### Credenciais da AWS

O projeto usa o `DefaultCredentialsProvider` do SDK, que procura credenciais nesta ordem: variáveis de ambiente (`AWS_ACCESS_KEY_ID`, `AWS_SECRET_ACCESS_KEY`, `AWS_SESSION_TOKEN`), arquivo `~/.aws/credentials`, perfil/SSO e role da instância (EC2/ECS).

```bash
export AWS_ACCESS_KEY_ID=...
export AWS_SECRET_ACCESS_KEY=...
export AWS_SESSION_TOKEN=...      # se estiver usando credenciais temporárias
export AWS_REGION=us-east-1
```

### Bucket

O nome do bucket e o prefixo estão fixos em `Main.java`:

```java
private static final String BUCKET_NAME = "datalake-gold-roadvizor-sptech";
private static final String PREFIXO = "";
```

Para usar outro bucket, altere essas constantes.

---

## Banco de dados

O `JiraRepository` executa a seguinte consulta para descobrir qual Jira usar:

```sql
SELECT e.domain_jira, e.email_jira, e.api_token_jira, e.project_key
FROM servidor s
JOIN empresa e ON s.empresa_id = e.id_empresa
WHERE s.endereco_mac = ?
```

Portanto, o banco precisa ter:

| Tabela     | Colunas utilizadas                                                          |
| ---------- | --------------------------------------------------------------------------- |
| `servidor` | `endereco_mac`, `empresa_id`                                                |
| `empresa`  | `id_empresa`, `domain_jira`, `email_jira`, `api_token_jira`, `project_key`  |

---

## Formato do arquivo no S3

Os arquivos devem se chamar `dados_AAAAMMDD_HHMMSS_N.json` e ter este formato:

```json
{
  "id_servidor": 1,
  "endereco_mac": "AA:BB:CC:DD:EE:FF",
  "apelido": "servidor-km-120",
  "empresa": "Concessionária X",
  "localizacao_km": "KM 120",
  "sentido": "Norte",
  "timestamp": "2026-10-24 18:30:00",
  "metricas_monitorizadas": [
    {
      "componente": "CPU",
      "unidade_medida": "%",
      "valor_medido": 95.4,
      "limite_min": 0.0,
      "limite_max": 90.0,
      "em_alerta": true
    }
  ]
}
```

O `timestamp` é lido no formato `yyyy-MM-dd HH:mm:ss`. Campos desconhecidos no JSON são ignorados.

---

## Como executar

### Compilar

```bash
mvn clean package
```

O plugin **Shade** gera um JAR único com todas as dependências em `target/s3-connection-1.0-SNAPSHOT.jar`.

### Rodar

```bash
export DB_USER=seu_usuario
export DB_PASSWORD=sua_senha
# export DB_URL=jdbc:mysql://host:3306/roadvizor   # opcional
export AWS_REGION=us-east-1

java -jar target/s3-connection-1.0-SNAPSHOT.jar
```

### Exemplo de saída

```
╔══════════════════════════════════════════════╗
║              CONEXÃO COM AMAZON S3          ║
...
Arquivo mais recente: dados_20261024_183000_1.json
...
Abrindo chamado no jira...
🚀 Chamado aberto com sucesso! Resposta do Jira: {...}
```

Possíveis mensagens finais:

- `✅ Todas as métricas estão normais. Nenhuma ação necessária.`
- `❌ Alerta gerado, mas nenhuma credencial do Jira foi encontrada no Banco!`
- `BUCKET VAZIO` / `Nenhum arquivo no padrão dados_AAAAMMDD_HHMMSS_N.json encontrado.`

---

## O chamado no Jira

O chamado é criado em `POST {dominio}/rest/api/3/issue` com autenticação Basic (e-mail + token de API) e contém:

- **Título:** `🚨 ALERTA: Componente [CPU] em nível crítico no servidor <apelido>`
- **Descrição:** servidor e MAC, empresa, localização/sentido, horário, componente, valor medido e limite máximo
- **Projeto:** o `project_key` da empresa
- **Tipo de chamado:** ID `10010` (fixo em `Reader.java`, ajuste conforme o seu Jira)

---

## Limitações conhecidas

- Apenas o **arquivo mais recente** é processado a cada execução, escolhido pela ordem alfabética do nome.
- Se houver várias métricas em alerta no mesmo arquivo, **só a primeira** gera chamado.
- Não há controle de duplicidade: executar o programa duas vezes sobre o mesmo arquivo abre dois chamados.
- O ID do tipo de chamado (`10010`) e o nome do bucket são fixos no código.

---

## Segurança

- **Nunca** versione credenciais da AWS, senhas do banco ou tokens do Jira. Use sempre variáveis de ambiente.
- O token de API do Jira fica armazenado na tabela `empresa`; restrinja o acesso a essa tabela e considere guardá-lo criptografado.
- Dê ao usuário/role da AWS apenas as permissões mínimas necessárias (leitura no bucket).

---
