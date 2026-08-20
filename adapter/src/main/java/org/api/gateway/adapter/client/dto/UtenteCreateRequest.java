package org.api.gateway.adapter.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UtenteCreateRequest {
    @JsonProperty("nomeUtente")
    private String nomeUtente;
    
    @JsonProperty("nome")
    private String nome;
    
    @JsonProperty("cognome")
    private String cognome;
    
    @JsonProperty("email")
    private String email;
    
    @JsonProperty("password")
    private String password;
}
