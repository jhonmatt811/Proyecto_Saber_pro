package com.icfes_group.dto;

import com.icfes_group.model.IcfesTest.ModuleCatalog;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ComparativeIcfesPersonDTO {
    private String nombreEstudiante;
    private List<Map<String,Object>> resultadosEstudiante;
    private List<Map<String,Object>> resultadosGrupo;
    private Double totalEstudiante;
}
