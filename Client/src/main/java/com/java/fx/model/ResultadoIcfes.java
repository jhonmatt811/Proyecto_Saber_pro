package com.java.fx.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ResultadoIcfes {
    private Integer periodo;
    @JsonProperty("tipoDocumento")
    private String tipoDocumento;
    @JsonProperty("numeroRegistro")
    private String numeroRegistro;
    @JsonProperty("sniesProgramaAcademico")
    private String sniesProgramaAcademico;
    @JsonProperty("tipoEvaluado")
    private String tipoEvaluado;
    private String programa;
    private String ciudad;
    private Integer razonamientoCuantitativo;
    private Integer lecturaCritica;
    private Integer comunicacionEscrita;
    private Integer comunicacionEscritaDesempeno;
    private Integer ingles;
    private String inglesDesempeno;
    private Integer competenciasCiudadanas;

    public Integer getPeriodo() {return periodo;}
    public String getTipoDocumento() {return tipoDocumento;}
    public String getNumeroRegistro() {return numeroRegistro;}
    public String getSniesProgramaAcademico() {return sniesProgramaAcademico;}
    public String getTipoEvaluado() {return tipoEvaluado;}
    public String getPrograma() {return programa;}
    public String getCiudad() {return ciudad;}
    public Integer getRazonamientoCuantitativo() {return razonamientoCuantitativo;}
    public Integer getLecturaCritica() {return lecturaCritica;}
    public Integer getComunicacionEscrita() {return comunicacionEscrita;}
    public Integer getComunicacionEscritaDesempeno() {return comunicacionEscritaDesempeno;}
    public Integer getIngles() {return ingles;}
    public String getInglesDesempeno() {return inglesDesempeno;}
    public Integer getCompetenciasCiudadanas() {return competenciasCiudadanas;}

    public void setPeriodo(Integer periodo) {this.periodo = periodo;}
    public void setTipoDocumento(String tipoDocumento) {this.tipoDocumento = tipoDocumento;}
    public void setNumeroRegistro(String numeroRegistro) {this.numeroRegistro = numeroRegistro;}
    public void setSniesProgramaAcademico(String sniesProgramaAcademico) {this.sniesProgramaAcademico = sniesProgramaAcademico;}
    public void setTipoEvaluado(String tipoEvaluado) {this.tipoEvaluado = tipoEvaluado;}
    public void setPrograma(String programa) {this.programa = programa;}
    public void setCiudad(String ciudad) {this.ciudad = ciudad;}
    public void setRazonamientoCuantitativo(Integer razonamientoCuantitativo) {this.razonamientoCuantitativo = razonamientoCuantitativo;}
    public void setLecturaCritica(Integer lecturaCritica) {this.lecturaCritica = lecturaCritica;}
    public void setComunicacionEscrita(Integer comunicacionEscrita) {this.comunicacionEscrita = comunicacionEscrita;}
    public void setComunicacionEscritaDesempeno(Integer comunicacionEscritaDesempeno) {this.comunicacionEscritaDesempeno = comunicacionEscritaDesempeno;}
    public void setIngles(Integer ingles) {this.ingles = ingles;}
    public void setInglesDesempeno(String inglesDesempeno) {this.inglesDesempeno = inglesDesempeno;}
    public void setCompetenciasCiudadanas(Integer competenciasCiudadanas) {this.competenciasCiudadanas = competenciasCiudadanas;}
}
