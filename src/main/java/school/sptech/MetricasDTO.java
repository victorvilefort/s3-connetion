package school.sptech;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MetricasDTO {
   @JsonProperty private String componente;
   @JsonProperty private String unidade_medida;
   @JsonProperty private Double valor_medido;
   @JsonProperty private Double limite_min;
   @JsonProperty private Double limite_max;
   @JsonProperty private Boolean em_alerta;
    {}
}
