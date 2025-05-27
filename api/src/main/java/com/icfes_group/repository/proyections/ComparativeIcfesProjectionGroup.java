package com.icfes_group.repository.proyections;

public interface ComparativeIcfesProjectionGroup {
    Double getPromedioGrupoGlobal();
    Double getPromedioGrupoModulo();
    Double getVarianzaGlobal();
    Double getVarianzaModulo();
    String getNombreModulo();
    String getNombreGrupoReferencia();
    String getProgramaAcademico();
}
