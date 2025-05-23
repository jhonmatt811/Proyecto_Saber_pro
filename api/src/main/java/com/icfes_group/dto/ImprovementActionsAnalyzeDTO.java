package com.icfes_group.dto;

import com.icfes_group.model.IcfesTest.ImprovementActions;
import lombok.Data;

@Data
public class ImprovementActionsAnalyzeDTO {
    private ImprovementActions accionMejora;
    private String message;
    private Double porcentajeMejora;
}
