package org.api.gateway.adapter.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UtenteRuoloUpdateRequest {
    @JsonProperty("idUtente")
    private Integer idUtente;
    
    @JsonProperty("idRuolo")
    private Integer idRuolo;
}
