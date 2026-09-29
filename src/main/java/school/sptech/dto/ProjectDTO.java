package school.sptech.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ProjectDTO {
    @JsonProperty("key")
    private String key;

    public ProjectDTO() {
    }

    public ProjectDTO(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
