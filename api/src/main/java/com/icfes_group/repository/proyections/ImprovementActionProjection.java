package com.icfes_group.repository.proyections;

import lombok.Value;

public interface ImprovementActionProjection {
    Double getPromedioInicio();
    Double getPromedioFinal();
    String getNombre();
}
