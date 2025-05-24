package com.icfes_group.integrate.gemini.config;

import com.icfes_group.dto.ImprovementActionsAnalyzeDTO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "spring.ai.vertex.ai.gemini")
@Getter
@Setter
public class GeminiIntegrationSettings {
    private String apiKey;
    private String url;

    public String buildPrompt(ImprovementActionsAnalyzeDTO dto){
        return String.format("""
            Actúa como un experto del ICFES en evaluación educativa con amplia experiencia en análisis de propuestas de mejora para programas universitarios en Colombia. Con base en los siguientes datos, realiza un análisis integral de una propuesta de mejora formulada por un programa académico.
            
            Tu respuesta debe incluir:
            
            1. Análisis de la pertinencia y el enfoque de la propuesta de mejora.
            2. Observación crítica y constructiva sobre la calidad de su formulación.
            3. Evaluación de su coherencia con el porcentaje de mejora observado (positivo o negativo).
            4. Redacción profesional de una nueva propuesta de mejora, adecuada para informes institucionales ante el ICFES.
            5. Recomendación concreta para el programa académico y/o el equipo docente.
            
            ### Datos para el análisis:
            
            - **Programa académico:** %s (SNIES: %s) \s
            - **Módulo evaluado:** %s \s
            - **Propuesta de mejora inicial:** "%s" \s
            - **Periodo evaluado:** %d a %d \s
            - **Porcentaje de mejora observado:** %.2f%%
            
            Utiliza un tono técnico, claro y profesional. Evita lenguaje coloquial. Presenta tu respuesta de forma estructurada.
            """,
                dto.getAccionMejora().getPrograma().getNombre(),
                dto.getAccionMejora().getPrograma().getSnies(),
                dto.getAccionMejora().getModulo().getNombre(),
                dto.getAccionMejora().getSugerenciaMejora(),
                dto.getAccionMejora().getYearInicio(),
                dto.getAccionMejora().getYearFin(),
                dto.getPorcentajeMejora()
        );
    }
}
