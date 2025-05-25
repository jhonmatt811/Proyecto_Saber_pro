package com.java.fx.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.java.fx.model.AccionesDeMejora.Modulo;
import com.java.fx.model.AccionesDeMejora.Programa;

public class Resultado {
    private int ciclo;
    private int year;
    private long documento;
    private String tipoDocumento;
    private String nombre;
    private String numeroRegistro;
    private String tipoEvaluado;
    private String sniesProgramaAcademico;
    private String programa;
    private String ciudad;
    private String nucleoBasicoConocimiento;
    private String puntajeGlobal;
    private String percentilNacionalGlobal;
    private String percentilNacionalNbc;
    private String modulo;
    private String puntajeModulo;
    @JsonProperty("nivelDesempeño")                               // mapea nombre con tilde
    private String nivelDesempeno;
    private String percentilNacionalModulo;
    private String percentilGrupoNbcModulo;
    private String novedades;

    //para acciones de mejora
    @JsonProperty("programaAM")
    private Programa programaAM; // Contiene id, snies, nombre
    @JsonProperty("moduloAM")
    private Modulo moduloAM;     // Contiene id, nombre

    public Programa getProgramaAM() {return programaAM;}
    public Modulo getModuloAM() {return moduloAM;}

    public void setProgramaAM(Programa programaAM) {this.programaAM = programaAM;}
    public void setModuloAM(Modulo moduloAM) {this.moduloAM = moduloAM;}

    // 1) Constructor vacío para Jackson
    public Resultado() { }

    // 2) Constructor “lleno” para el CSV
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

    //setters
    public void setCiclo(int ciclo) {
        this.ciclo = ciclo;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setDocumento(long documento) {
        this.documento = documento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setNumeroRegistro(String numeroRegistro) {
        this.numeroRegistro = numeroRegistro;
    }

    public void setTipoEvaluado(String tipoEvaluado) {
        this.tipoEvaluado = tipoEvaluado;
    }

    public void setSniesProgramaAcademico(String sniesProgramaAcademico) {
        this.sniesProgramaAcademico = sniesProgramaAcademico;
    }

    public void setPrograma(String programa) {
        this.programa = programa;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public void setNucleoBasicoConocimiento(String nucleoBasicoConocimiento) {
        this.nucleoBasicoConocimiento = nucleoBasicoConocimiento;
    }

    public void setPuntajeGlobal(String puntajeGlobal) {
        this.puntajeGlobal = puntajeGlobal;
    }

    public void setPercentilNacionalGlobal(String percentilNacionalGlobal) {
        this.percentilNacionalGlobal = percentilNacionalGlobal;
    }

    public void setPercentilNacionalNbc(String percentilNacionalNbc) {
        this.percentilNacionalNbc = percentilNacionalNbc;
    }

    public void setModulo(String modulo) {
        this.modulo = modulo;
    }

    public void setPuntajeModulo(String puntajeModulo) {
        this.puntajeModulo = puntajeModulo;
    }

    public void setNivelDesempeno(String nivelDesempeno) {
        this.nivelDesempeno = nivelDesempeno;
    }

    public void setPercentilNacionalModulo(String percentilNacionalModulo) {
        this.percentilNacionalModulo = percentilNacionalModulo;
    }

    public void setPercentilGrupoNbcModulo(String percentilGrupoNbcModulo) {
        this.percentilGrupoNbcModulo = percentilGrupoNbcModulo;
    }

    public void setNovedades(String novedades) {
        this.novedades = novedades;
    }
}