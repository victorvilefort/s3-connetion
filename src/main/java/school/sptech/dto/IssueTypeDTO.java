package school.sptech.dto;

public class IssueTypeDTO {
    private String name;

    public IssueTypeDTO() {
    }

    public IssueTypeDTO(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
