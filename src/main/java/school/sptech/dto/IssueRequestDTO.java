package school.sptech.dto;

public class IssueRequestDTO {
    private IssueFieldsDTO fields;

    public IssueRequestDTO() {
    }

    public IssueRequestDTO(IssueFieldsDTO fields) {
        this.fields = fields;
    }

    public IssueFieldsDTO getFields() {
        return fields;
    }

    public void setFields(IssueFieldsDTO fields) {
        this.fields = fields;
    }
}
