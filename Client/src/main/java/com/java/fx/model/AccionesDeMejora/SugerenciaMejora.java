package com.java.fx.model.AccionesDeMejora;

public class SugerenciaMejora {
    private Programa programa;
    private Modulo modulo;
    private String sugerenciaMejora;
    private int yearInicio;
    private int yearFin;

    public Programa getPrograma() {return programa;}
    public Modulo getModulo() {return modulo;}
    public String getSugerenciaMejora() {return sugerenciaMejora;}
    public int getYearInicio() {return yearInicio;}
    public int getYearFin() {return yearFin;}

    public void setPrograma(Programa programa) {this.programa = programa;}
    public void setModulo(Modulo modulo) {this.modulo = modulo;}
    public void setSugerenciaMejora(String sugerenciaMejora) {this.sugerenciaMejora = sugerenciaMejora;}
    public void setYearInicio(int yearInicio) {this.yearInicio = yearInicio;}
    public void setYearFin(int yearFin) {this.yearFin = yearFin;}
}
