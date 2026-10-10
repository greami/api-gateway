package org.api.gateway.application.model;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public record GatewayResponse<T>(int statusCode, Map<String, List<String>> headers, T body) {
    public GatewayResponse {
        if (statusCode < 100 || statusCode > 599) {
            throw new IllegalArgumentException("Invalid HTTP status code: " + statusCode);
        }

        Map<String, List<String>> copiedHeaders = new LinkedHashMap<>();
        Objects.requireNonNull(headers, "headers")
                .forEach((name, values) -> copiedHeaders.put(name, List.copyOf(values)));
        headers = Collections.unmodifiableMap(copiedHeaders);
    }

    public static <T> GatewayResponse<T> of(int statusCode, T body) {
        return new GatewayResponse<>(statusCode, Map.of(), body);
    }
}
