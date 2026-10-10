package org.api.gateway.adapter.port.out;

import org.api.gateway.application.model.GatewayResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class GatewayResponseMapper {
    private GatewayResponseMapper() {
    }

    public static <T> GatewayResponse<T> from(ResponseEntity<T> response) {
        Map<String, List<String>> headers = new LinkedHashMap<>();
        response.getHeaders().forEach((name, values) -> headers.put(name, List.copyOf(values)));
        return new GatewayResponse<>(response.getStatusCode().value(), headers, response.getBody());
    }

    public static <T> ResponseEntity<T> toResponseEntity(GatewayResponse<T> response) {
        HttpHeaders headers = new HttpHeaders();
        response.headers().forEach(headers::put);
        return new ResponseEntity<>(response.body(), headers, HttpStatusCode.valueOf(response.statusCode()));
    }
}
