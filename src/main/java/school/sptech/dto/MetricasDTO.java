package school.sptech.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MetricasDTO {
   @JsonProperty private String componente;
   @JsonProperty private String unidade_medida;
   @JsonProperty private Double valor_medido;
   @JsonProperty private Double limite_min;
   @JsonProperty private Double limite_max;
   @JsonProperty private Boolean em_alerta;

   public MetricasDTO() {
   }

   public String getComponente() {
      return componente;
   }

   public void setComponente(String componente) {
      this.componente = componente;
   }

   public String getUnidade_medida() {
      return unidade_medida;
   }

   public void setUnidade_medida(String unidade_medida) {
      this.unidade_medida = unidade_medida;
   }

   public Double getValor_medido() {
      return valor_medido;
   }

   public void setValor_medido(Double valor_medido) {
      this.valor_medido = valor_medido;
   }

   public Double getLimite_min() {
      return limite_min;
   }

   public void setLimite_min(Double limite_min) {
      this.limite_min = limite_min;
   }

   public Double getLimite_max() {
      return limite_max;
   }

   public void setLimite_max(Double limite_max) {
      this.limite_max = limite_max;
   }

   public Boolean getEm_alerta() {
      return em_alerta;
   }

   public void setEm_alerta(Boolean em_alerta) {
      this.em_alerta = em_alerta;
   }
}
