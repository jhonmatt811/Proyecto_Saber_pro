package com.java.fx.model.AccionesDeMejora;

public class AnalisisMejora {

    private SugerenciaMejora accionMejora;
    private String message;
    private double porcentajeMejora;


    public SugerenciaMejora getAccionMejora() {return accionMejora;}
    public String getMessage() {return message;}
    public double getPorcentajeMejora() {return porcentajeMejora;}

    public void setAccionMejora(SugerenciaMejora accionMejora) {this.accionMejora = accionMejora;}
    public void setMessage(String message) {this.message = message;}
    public void setPorcentajeMejora(double porcentajeMejora) {this.porcentajeMejora = porcentajeMejora;}
}
