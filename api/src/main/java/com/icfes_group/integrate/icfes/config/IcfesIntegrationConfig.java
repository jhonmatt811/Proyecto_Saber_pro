package com.icfes_group.integrate.icfes.config;

import lombok.Data;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

@Component
@Data
public class IcfesIntegrationConfig {
    private String ICFES_URL = "https://www.datos.gov.co/resource/u37r-hjmu.json";

    @Value("${ICFES_TOKEN}")
    private String ICFES_TOKEN;
    @Value("${ICFES_EMAIL}")
    private String ICFES_EMAIL;
    @Value("${ICFES_PASSWORD}")
    private String ICFES_PASSWORD;

    public Map<String, String> bodyRequest() {
        return Map.of(
                "api_key", ICFES_TOKEN,
                "email", ICFES_EMAIL,
                "password", ICFES_PASSWORD
        );
    }

    public String buildUrlWithParams(Map<String, String> params) {
        StringBuilder url = new StringBuilder(this.getICFES_URL());
        if (params != null && !params.isEmpty()) {
            url.append("?");
            url.append(params.entrySet().stream()
                    .map(e -> e.getKey() + "=" + e.getValue())
                    .collect(Collectors.joining("&")));
        }
        return url.toString();
    }
}
