package com.java.fx.model.AccionesDeMejora;

public class AnalisisMejora {

    private SugerenciaMejora accionMejora;
    private String message;
    private Double porcentajeMejora;


    public SugerenciaMejora getAccionMejora() {return accionMejora;}
    public String getMessage() {return message;}
    public Double getPorcentajeMejora() {
        return (porcentajeMejora != null &&
                !porcentajeMejora.isInfinite() &&
                !porcentajeMejora.isNaN()) ? porcentajeMejora : null;
    }
    public void setAccionMejora(SugerenciaMejora accionMejora) {this.accionMejora = accionMejora;}
    public void setMessage(String message) {this.message = message;}
    public void setPorcentajeMejora(Double porcentajeMejora) {this.porcentajeMejora = porcentajeMejora;}
}
