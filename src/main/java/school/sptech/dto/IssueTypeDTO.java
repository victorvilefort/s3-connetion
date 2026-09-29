package school.sptech.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class IssueTypeDTO {
    @JsonProperty("id")
    String id;

    public IssueTypeDTO() {
    }

    public IssueTypeDTO(String id) {
        this.id = id    ;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
