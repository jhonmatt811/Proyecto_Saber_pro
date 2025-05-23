package com.icfes_group.integrate.gemini.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.icfes_group.dto.ImprovementActionsAnalyzeDTO;
import com.icfes_group.integrate.gemini.config.GeminiIntegrationSettings;
import lombok.AllArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Map;

@AllArgsConstructor
@Service
public class GeminiIntegrationService {
    private final RestTemplate restTemplate;
    private final GeminiIntegrationSettings geminiIntegrationSettings;
    public String getGeminiData(ImprovementActionsAnalyzeDTO params) throws JsonProcessingException {
        String url = geminiIntegrationSettings.getUrl() + geminiIntegrationSettings.getApiKey();
        String prompt = geminiIntegrationSettings.buildPrompt(params);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Construir el cuerpo (payload) en formato JSON:
        // { "contents": [ { "parts": [ { "text": "<prompt>" } ] } ] }
        Map<String, Object> part = Collections.singletonMap("text", prompt);
        Map<String, Object> content = Collections.singletonMap("parts", Collections.singletonList(part));
        Map<String, Object> body = Collections.singletonMap("contents", Collections.singletonList(content));

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        // Enviar la petición POST al endpoint de Gemini
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
        if (response.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("Error en la petición Gemini: " + response.getStatusCode());
        }

        // Parsear la respuesta JSON para extraer el texto generado
        String json = response.getBody();
        JsonNode root = new ObjectMapper().readTree(json);
        // La respuesta tiene la forma {"candidates":[{"content":{"parts":[{"text":"<respuesta>"}]}}]}
        JsonNode textNode = root.at("/candidates/0/content/parts/0/text");
        return textNode.asText();
    }
}
