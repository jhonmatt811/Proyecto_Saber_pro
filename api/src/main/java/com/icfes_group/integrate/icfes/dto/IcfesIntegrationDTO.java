package com.icfes_group.integrate.icfes.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

/**
 *
 * @author juanc
 *
 * Esta clase aunque parecida a @{@link com.icfes_group.dto.ScoreFileDTO}, no son iguales
 * esta lee los datos y atributos que responde la api publica del icfes, de modo que el mapeo de la misma es distinto:
 */

@Data
public class IcfesIntegrationDTO {
    private Integer periodo;

    @JsonAlias("estu_tipodocumento")
    private String tipoDocumento;

    @JsonAlias("estu_consecutivo")
    private String numeroRegistro;

    @JsonAlias("estu_snies_prgmacademico")
    private String sniesProgramaAcademico;

    @JsonAlias("estu_estudiante")
    private String tipoEvaluado;

    @JsonAlias("estu_prgm_academico")
    private String programa;

    @JsonAlias("estu_mcpio_presentacion")
    private String ciudad;

    @JsonAlias("mod_razona_cuantitat_punt")
    private Integer razonamientoCuantitativo;

    @JsonAlias("mod_lectura_critica_punt")
    private Integer lecturaCritica;

    @JsonAlias("mod_comuni_escrita_punt")
    private Integer comunicacionEscrita;

    @JsonAlias("mod_comuni_escrita_desem")
    private Integer comunicacionEscritaDesempeno;

    @JsonAlias("mod_ingles_punt")
    private Integer ingles;

    @JsonAlias("mod_ingles_desem")
    private String inglesDesempeno;

    @JsonAlias("mod_competen_ciudada_punt")
    private Integer competenciasCiudadanas;
}