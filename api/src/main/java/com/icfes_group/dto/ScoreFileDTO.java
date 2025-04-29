/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.icfes_group.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 *
 * @author juanc
 */
@Data
public class ScoreFileDTO {

      @NotBlank(message = "Periodo no definido")
    private String periodo; // <-- Corregido a String

    @NotBlank(message = "Aplicación no definida")
    private String aplicacion;

    @NotBlank(message = "Examen no definido")
    private String examen;

    @NotBlank(message = "Tipo de documento no definido")
    private String tipoDocumento;

    @NotNull(message = "Documento no definido")
    private Long documento; // <-- Numérico

    @NotBlank(message = "Nombre no definido")
    private String nombre;

    @NotBlank(message = "Número de registro no definido")
    private String numeroRegistro; // <-- Es string en el JSON ("EK202220401802")

    @NotBlank(message = "Tipo de evaluado no definido")
    private String tipoEvaluado;

    @NotNull(message = "SNIES IES no definido")
    private Integer sniesIes; // <-- Es numérico, en el JSON es 1119

    @NotBlank(message = "IES no definido")
    private String ies; // <-- En el JSON viene el nombre de la universidad

    @NotNull(message = "SNIES programa académico no definido")
    private Integer sniesProgramaAcademico; // <-- Numérico

    @NotBlank(message = "Programa no definido")
    private String programa;

    @NotBlank(message = "Ciudad no definida")
    private String ciudad;

    @NotNull(message = "Id núcleo básico de conocimiento no definido")
    private Integer idNucleoBasicoConocimiento;

    @NotBlank(message = "Núcleo básico de conocimiento no definido")
    private String nucleoBasicoConocimiento;

    @NotNull(message = "Puntaje global no definido")
    private Integer puntajeGlobal;

    @NotNull(message = "Percentil nacional global no definido")
    private Integer percentilNacionalGlobal;

    @NotNull(message = "Percentil nacional NBC no definido")
    private Integer percentilNacionalNbc;

    @NotBlank(message = "Módulo no definido")
    private String modulo;

    @NotNull(message = "Puntaje del módulo no definido")
    private Integer puntajeModulo;

    @NotBlank(message = "Nivel de desempeño no definido")
    private String nivelDesempeño; // <-- Siempre texto ("2", "B2", etc.)

    @NotNull(message = "Percentil nacional del módulo no definido")
    private Integer percentilNacionalModulo;

    @NotNull(message = "Percentil grupo NBC módulo no definido")
    private Integer percentilGrupoNbcModulo;

    // Este sí puede ser nulo
    private String novedades;
}
