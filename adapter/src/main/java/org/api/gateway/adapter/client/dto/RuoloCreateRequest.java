package org.api.gateway.adapter.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RuoloCreateRequest {
    @JsonProperty("idRuolo")
    private Integer idRuolo;
    
    @JsonProperty("nome")
    private String nome;
    
    @JsonProperty("descrizione")
    private String descrizione;
}
