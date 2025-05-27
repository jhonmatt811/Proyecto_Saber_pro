package com.java.fx.model.dto; // Asegúrate de que el paquete coincida con tu estructura

import java.util.List;

public class ComparacionResponse {
    private String nombreEstudiante;
    private List<ResultadoEstudiante> resultadosEstudiante;
    private List<ModuloGrupo> resultadosGrupo;
    private double totalEstudiante;

    // Getters y Setters (Obligatorios para Gson)
    public String getNombreEstudiante() { return nombreEstudiante; }
    public void setNombreEstudiante(String nombreEstudiante) { this.nombreEstudiante = nombreEstudiante; }

    public List<ResultadoEstudiante> getResultadosEstudiante() { return resultadosEstudiante; }
    public void setResultadosEstudiante(List<ResultadoEstudiante> resultadosEstudiante) { this.resultadosEstudiante = resultadosEstudiante; }

    public List<ModuloGrupo> getResultadosGrupo() { return resultadosGrupo; }
    public void setResultadosGrupo(List<ModuloGrupo> resultadosGrupo) { this.resultadosGrupo = resultadosGrupo; }

    public double getTotalEstudiante() { return totalEstudiante; }
    public void setTotalEstudiante(double totalEstudiante) { this.totalEstudiante = totalEstudiante; }
}