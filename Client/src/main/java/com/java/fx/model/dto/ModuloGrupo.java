package com.java.fx.model.dto;

public class ModuloGrupo {
    private String nombreModulo;
    private double promedioGrupoModulo;
    private double varianzaGlobal;
    private double promedioGrupoGlobal;
    private double varianzaModulo;

    // Getters y Setters
    public String getNombreModulo() { return nombreModulo; }
    public void setNombreModulo(String nombreModulo) { this.nombreModulo = nombreModulo; }

    public double getPromedioGrupoModulo() { return promedioGrupoModulo; }
    public void setPromedioGrupoModulo(double promedioGrupoModulo) { this.promedioGrupoModulo = promedioGrupoModulo; }

    public double getVarianzaGlobal() { return varianzaGlobal; }
    public void setVarianzaGlobal(double varianzaGlobal) { this.varianzaGlobal = varianzaGlobal; }

    public double getPromedioGrupoGlobal() { return promedioGrupoGlobal; }
    public void setPromedioGrupoGlobal(double promedioGrupoGlobal) { this.promedioGrupoGlobal = promedioGrupoGlobal; }

    public double getVarianzaModulo() { return varianzaModulo; }
    public void setVarianzaModulo(double varianzaModulo) { this.varianzaModulo = varianzaModulo; }
}