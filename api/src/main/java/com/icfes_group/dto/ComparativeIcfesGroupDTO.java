package com.icfes_group.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ComparativeIcfesGroupDTO {
    private String nombrePrograma;
    private List<Map<String, Object>> resultados;
    private List<Map<String, Object>> resultadosGrupo;
}
