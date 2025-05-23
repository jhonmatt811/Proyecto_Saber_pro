package com.icfes_group.dto;

import com.icfes_group.model.IcfesTest.AcademicProgram;
import com.icfes_group.model.IcfesTest.ImprovementActions;
import com.icfes_group.model.IcfesTest.ModuleCatalog;
import com.icfes_group.repository.proyections.ImprovementActionProjection;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ImprovementActionsDTO {
    @NotEmpty(message = "Debe indicar el programa")
    private AcademicProgram programa;
    @NotEmpty(message = "Debe indicar el modulo")
    private ModuleCatalog modulo;
    @NotBlank(message = "Debe indicar la sugerencia")
    private String sugerenciaMejora;

    @NotNull(message = "Debe indicar el año de inicio")
    private Integer yearInicio;
    @NotNull(message = "Debe indicar el año de fin")
    private Integer yearFin;
}
