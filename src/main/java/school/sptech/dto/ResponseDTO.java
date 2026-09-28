package school.sptech.dto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseDTO {

    @JsonProperty private Integer id_servidor;
    @JsonProperty private String endereco_mac;
    @JsonProperty private String apelido;
    @JsonProperty private String empresa;
    @JsonProperty private String localizacao_km;
    @JsonProperty private String sentido;
    @JsonProperty private java.time.LocalDateTime timestamp;
    @JsonProperty private List<MetricasDTO> metricas_monitorizadas;

    public ResponseDTO() {
    }

    public Integer getId_servidor() {
        return id_servidor;
    }

    public void setId_servidor(Integer id_servidor) {
        this.id_servidor = id_servidor;
    }

    public String getEndereco_mac() {
        return endereco_mac;
    }

    public void setEndereco_mac(String endereco_mac) {
        this.endereco_mac = endereco_mac;
    }

    public String getApelido() {
        return apelido;
    }

    public void setApelido(String apelido) {
        this.apelido = apelido;
    }

    public String getEmpresa() {
        return empresa;
    }

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }

    public String getLocalizacao_km() {
        return localizacao_km;
    }

    public void setLocalizacao_km(String localizacao_km) {
        this.localizacao_km = localizacao_km;
    }

    public String getSentido() {
        return sentido;
    }

    public void setSentido(String sentido) {
        this.sentido = sentido;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public List<MetricasDTO> getMetricas_monitorizadas() {
        return metricas_monitorizadas;
    }

    public void setMetricas_monitorizaradas(List<MetricasDTO> metricas_monitorizaradas) {
        this.metricas_monitorizadas = metricas_monitorizaradas;
    }
}
