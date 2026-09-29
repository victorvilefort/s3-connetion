package school.sptech.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Collections;
import java.util.List;

public class IssueFieldsDTO {

    @JsonProperty("project")
    private ProjectDTO project;

    @JsonProperty("summary")
    private String summary;

    @JsonProperty("issuetype")
    private IssueTypeDTO issuetype;

    @JsonProperty("description")
    private AdfDocumentDTO description; // Mapeia para a descrição oficial do Jira v3

    public IssueFieldsDTO() {
    }

    // CONSTRUTOR CORRIGIDO: Aceita todos os parâmetros na ordem certa
    public IssueFieldsDTO(ProjectDTO project, String summary, String textBlock, IssueTypeDTO issuetype) {
        this.project = project;
        this.summary = summary;
        this.issuetype = issuetype;
        this.description = new AdfDocumentDTO(textBlock); // Transforma o bloco em formato ADF
    }

    // --- Estrutura de classes internas para gerar o JSON do ADF automaticamente ---
    public static class AdfDocumentDTO {
        @JsonProperty("type")
        public final String type = "doc";
        @JsonProperty("version")
        public final int version = 1;
        @JsonProperty("content")
        private List<AdfParagraphDTO> content;

        public AdfDocumentDTO(String text) {
            this.content = Collections.singletonList(new AdfParagraphDTO(text));
        }
        public List<AdfParagraphDTO> getContent() { return content; }
    }

    public static class AdfParagraphDTO {
        @JsonProperty("type")
        public final String type = "paragraph";
        @JsonProperty("content")
        private List<AdfTextDTO> content;

        public AdfParagraphDTO(String text) {
            this.content = Collections.singletonList(new AdfTextDTO(text));
        }

        public List<AdfTextDTO> getContent() {
            return content;
        }

    }

    public static class AdfTextDTO {
        @JsonProperty("type")
        public final String type = "text";
        @JsonProperty("text")
        private String text;

        public AdfTextDTO(String text) { this.text = text; }
        public String getText() { return text; }
    }

    public ProjectDTO getProject() { return project; }
    public void setProject(ProjectDTO project) { this.project = project; }
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    public IssueTypeDTO getIssuetype() { return issuetype; }
    public void setIssuetype(IssueTypeDTO issuetype) { this.issuetype = issuetype; }
    public AdfDocumentDTO getDescription() { return description; }
    public void setDescription(AdfDocumentDTO description) { this.description = description; }
}
