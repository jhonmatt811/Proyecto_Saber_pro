package com.java.fx.model.AccionesDeMejora;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GetAnalisisMejora {
    private String id;
    private Programa programa;
    private Modulo modulo;

    @JsonProperty("improvementProporsal")
    private String improvementProporsal;
    private int yearInicio;
    private int yearFin;

    public void setYearInicio(int yearInicio) {
        this.yearInicio = yearInicio;
    }

    public void setYearFin(int yearFin) {
        this.yearFin = yearFin;
    }

    public int getYearInicio() {
        return yearInicio;
    }

    public int getYearFin() {
        return yearFin;
    }

    public String getId() {
        return id;
    }

    public Programa getPrograma() {
        return programa;
    }

    public Modulo getModulo() {
        return modulo;
    }

    public String getImprovementProporsal() {
        return improvementProporsal;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setPrograma(Programa programa) {
        this.programa = programa;
    }

    public void setModulo(Modulo modulo) {
        this.modulo = modulo;
    }

    public void setImprovementProporsal(String improvementProporsal) {
        this.improvementProporsal = improvementProporsal;
    }
}
