package com.java.fx.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Resultado {
    private final int ciclo;
    private final int year;
    private final long documento;
    private final String tipoDocumento;
    private final String nombre;
    private final String numeroRegistro;
    private final String tipoEvaluado;
    private final String sniesProgramaAcademico;
    private final String programa;
    private final String ciudad;
    private final String nucleoBasicoConocimiento;
    private final String puntajeGlobal;
    private final String percentilNacionalGlobal;
    private final String percentilNacionalNbc;
    private final String modulo;
    private final String puntajeModulo;
    @JsonProperty("nivelDesempeño")                               // mapea nombre con tilde
    private final String nivelDesempeno;
    private final String percentilNacionalModulo;
    private final String percentilGrupoNbcModulo;
    private final String novedades;

    public Resultado(int ciclo,
                     int year, String tipoDocumento, long documento,
                     String nombre, String numeroRegistro,
                     String tipoEvaluado, String sniesProgramaAcademico,
                     String programa, String ciudad, String nucleoBasicoConocimiento,
                     String puntajeGlobal, String percentilNacionalGlobal,
                     String percentilNacionalNbc, String modulo,
                     String puntajeModulo, String nivelDesempeno,
                     String percentilNacionalModulo, String percentilGrupoNbcModulo,
                     String novedades) {
        this.ciclo = ciclo;
        this.year = year;
        this.tipoDocumento = tipoDocumento;
        this.documento = documento;
        this.nombre = nombre;
        this.numeroRegistro = numeroRegistro;
        this.tipoEvaluado = tipoEvaluado;
        this.sniesProgramaAcademico = sniesProgramaAcademico;
        this.programa = programa;
        this.ciudad = ciudad;
        this.nucleoBasicoConocimiento = nucleoBasicoConocimiento;
        this.puntajeGlobal = puntajeGlobal;
        this.percentilNacionalGlobal = percentilNacionalGlobal;
        this.percentilNacionalNbc = percentilNacionalNbc;
        this.modulo = modulo;
        this.puntajeModulo = puntajeModulo;
        this.nivelDesempeno = nivelDesempeno;
        this.percentilNacionalModulo = percentilNacionalModulo;
        this.percentilGrupoNbcModulo = percentilGrupoNbcModulo;
        this.novedades = novedades;
    }
    // Getters
    public int getCiclo() { return ciclo; }
    public int getYear() { return year; }
    public long getDocumento() { return documento; }
    public String getTipoDocumento() { return tipoDocumento; }
    public String getNombre() { return nombre; }
    public String getNumeroRegistro() { return numeroRegistro; }
    public String getTipoEvaluado() { return tipoEvaluado; }
    public String getSniesProgramaAcademico() { return sniesProgramaAcademico; }
    public String getPrograma() { return programa; }
    public String getCiudad() { return ciudad; }
    public String getNucleoBasicoConocimiento() { return nucleoBasicoConocimiento; }
    public String getPuntajeGlobal() { return puntajeGlobal; }
    public String getPercentilNacionalGlobal() { return percentilNacionalGlobal; }
    public String getPercentilNacionalNbc() { return percentilNacionalNbc; }
    public String getModulo() { return modulo; }
    public String getPuntajeModulo() { return puntajeModulo; }
    public String getNivelDesempeno() { return nivelDesempeno; }
    public String getPercentilNacionalModulo() { return percentilNacionalModulo; }
    public String getPercentilGrupoNbcModulo() { return percentilGrupoNbcModulo; }
    public String getNovedades() { return novedades; }
}