package com.icfes_group.service;

import com.icfes_group.integrate.icfes.config.IcfesIntegrationConfig;
import com.icfes_group.integrate.icfes.dto.IcfesIntegrationDTO;
import lombok.AllArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class IcfesIntegrationService {
    private final IcfesIntegrationConfig icfesIntegrationConfig;
    public List<IcfesIntegrationDTO> getIcfesData(Map<String, String> queryParams) {
        RestTemplate restTemplate = new RestTemplate();

        String fullUrl = icfesIntegrationConfig.buildUrlWithParams(queryParams);
        // Crear los headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-App-Token", icfesIntegrationConfig.getICFES_TOKEN()); // o el nombre real del header

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<List<IcfesIntegrationDTO>> response = restTemplate.exchange(
                fullUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<IcfesIntegrationDTO>>() {}
        );

        return response.getBody();
    }


}
