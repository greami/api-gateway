package org.api.gateway.adapter.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UtenteRuoloResponse {
    @JsonProperty("idUtenteRuolo")
    private Integer idUtenteRuolo;
    
    @JsonProperty("idUtente")
    private Integer idUtente;
    
    @JsonProperty("idRuolo")
    private Integer idRuolo;
}
