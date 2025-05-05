/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.icfes_group.repository.proyections;

/**
 *
 * @author juanc
 */
import org.springframework.beans.factory.annotation.Value;

public interface ScoreFileProjection {

    @Value("#{target.numeroRegistro}")
    String getNumeroRegistro();

    @Value("#{target.nombre}")
    String getNombre();

    @Value("#{target.documento}")
    Long getDocumento();

    @Value("#{target.tipoEvaluado}")
    String getTipoEvaluado();

    @Value("#{target.tipoDocumento}")
    String getTipoDocumento();

    @Value("#{target.programa}")
    String getPrograma();

    @Value("#{target.ciudad}")
    String getCiudad();

    @Value("#{target.puntajeGlobal}")
    String getPuntajeGlobal();

    @Value("#{target.percentilNacionalGlobal}")
    String getPercentilNacionalGlobal();

    @Value("#{target.percentilNacionalNbc}")
    String getPercentilNacionalNbc();

    @Value("#{target.modulo}")
    String getModulo();

    @Value("#{target.puntajeModulo}")
    String getPuntajeModulo();

    @Value("#{target.nivelDesempeño}")
    String getNivelDesempeno();

    @Value("#{target.percentilNacionalModulo}")
    String getPercentilNacionalModulo();

    @Value("#{target.percentilGrupoNbcModulo}")
    String getPercentilGrupoNbcModulo();

    @Value("#{target.novedades}")
    String getNovedades();
    
    //@Value("#{target.periodo}")
    //String getPeriodo();

    //@Value("#{target.aplicacion}")
    //String getAplicacion();

    //@Value("#{target.examen}")
    //String getExamen();

    //@Value("#{target.sniesIes}")
    //Integer getSniesIes();

    //@Value("#{target.ies}")
    //String getIes();

    @Value("#{target.sniesProgramaAcademico}")
    String getSniesProgramaAcademico();

    @Value("#{target.idNucleoBasicoConocimiento}")
    Long getIdNucleoBasicoConocimiento();

    @Value("#{target.nucleoBasicoConocimiento}")
    String getNucleoBasicoConocimiento();

    @Value("#{target.ciclo}")
    Integer getCiclo();

    @Value("#{target.year}")
    Integer getYear();
}