package com.icfes_group.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ComparativeAcademicProgramDTO {
    private String nombrePrograma;
    private List<Map<String,Object>> resultadosPrograma;
    private List<Map<String,Object>> resultadosGrupo;
}
