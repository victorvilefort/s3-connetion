package school.sptech.dto;

public class IssueFieldsDTO {

    private ProjectDTO project;
    private String summary;
    private IssueTypeDTO issuetype;

    public IssueFieldsDTO() {
    }

    public IssueFieldsDTO(ProjectDTO project, String summary, IssueTypeDTO issuetype) {
        this.project = project;
        this.summary = summary;
        this.issuetype = issuetype;
    }

    public ProjectDTO getProject() {
        return project;
    }

    public void setProject(ProjectDTO project) {
        this.project = project;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public IssueTypeDTO getIssuetype() {
        return issuetype;
    }

    public void setIssuetype(IssueTypeDTO issuetype) {
        this.issuetype = issuetype;
    }
}
