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
    Integer getPuntajeGlobal();

    @Value("#{target.percentilNacionalGlobal}")
    Integer getPercentilNacionalGlobal();

    @Value("#{target.percentilNacionalNbc}")
    Integer getPercentilNacionalNbc();

    @Value("#{target.modulo}")
    String getModulo();

    @Value("#{target.puntajeModulo}")
    Integer getPuntajeModulo();

    @Value("#{target.nivelDesempeño}")
    String getNivelDesempeno();

    @Value("#{target.percentilNacionalModulo}")
    Integer getPercentilNacionalModulo();

    @Value("#{target.percentilGrupoNbcModulo}")
    Integer getPercentilGrupoNbcModulo();

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
    Integer getSniesProgramaAcademico();

    @Value("#{target.idNucleoBasicoConocimiento}")
    Long getIdNucleoBasicoConocimiento();

    @Value("#{target.nucleoBasicoConocimiento}")
    String getNucleoBasicoConocimiento();

}