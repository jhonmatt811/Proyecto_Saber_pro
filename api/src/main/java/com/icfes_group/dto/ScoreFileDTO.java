package com.icfes_group.dto;

import com.icfes_group.repository.proyections.ScoreFileProjection;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 *
 * @author juanc
 *
 * Esta clase aunque parecida a @{@link com.icfes_group.integrate.icfes.dto.IcfesIntegrationDTO}, no son iguales
 * esta lee y envia los datos tal cual como estan descritos a continuacion:
 */
@Data
public class ScoreFileDTO {

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

    @NotBlank(message = "SNIES programa académico no definido")
    private String sniesProgramaAcademico; // <-- Numérico

    @NotBlank(message = "Programa no definido")
    private String programa;

    @NotBlank(message = "Ciu    dad no definida")
    private String ciudad;

    @NotBlank(message = "Núcleo básico de conocimiento no definido")
    private String nucleoBasicoConocimiento;

    @NotBlank(message = "Puntaje global no definido")
    private String puntajeGlobal;

    @NotNull(message = "Ciclo módulo no definido")
    private Integer ciclo;

    @NotNull(message = "year módulo no definido")
    private Integer year;

    @NotBlank(message = "Percentil nacional global no definido")
    private String percentilNacionalGlobal;

    @NotBlank(message = "Percentil nacional NBC no definido")
    private String percentilNacionalNbc;

    @NotBlank(message = "Módulo no definido")
    private String modulo;

    @NotBlank(message = "Puntaje del módulo no definido")
    private String puntajeModulo;

    @NotBlank(message = "Nivel de desempeño no definido")
    private String nivelDesempeño; // <-- Siempre texto ("2", "B2", etc.)

    @NotBlank(message = "Percentil nacional del módulo no definido")
    private String percentilNacionalModulo;

    @NotBlank(message = "Percentil grupo NBC módulo no definido")
    private String percentilGrupoNbcModulo;

    // Este sí puede ser nulo
    private String novedades;

    // Constructor con todos los campos
    public ScoreFileDTO(ScoreFileProjection pr) {
        this.tipoDocumento = pr.getTipoDocumento();
        this.documento = pr.getDocumento();
        this.nombre = pr.getNombre();
        this.numeroRegistro = pr.getNumeroRegistro();
        this.tipoEvaluado = pr.getTipoEvaluado();
        this.sniesProgramaAcademico = pr.getSniesProgramaAcademico();
        this.programa = pr.getPrograma();
        this.ciudad = pr.getCiudad();
        this.nucleoBasicoConocimiento = pr.getNucleoBasicoConocimiento();
        this.puntajeGlobal = pr.getPuntajeGlobal();
        this.percentilNacionalGlobal = pr.getPercentilNacionalGlobal();
        this.percentilNacionalNbc = pr.getPercentilNacionalNbc();
        this.modulo = pr.getModulo();
        this.puntajeModulo = pr.getPuntajeModulo();
        this.nivelDesempeño = pr.getNivelDesempeno();
        this.percentilNacionalModulo = pr.getPercentilNacionalModulo();
        this.percentilGrupoNbcModulo = pr.getPercentilGrupoNbcModulo();
        this.novedades = pr.getNovedades();
        this.ciclo = pr.getCiclo();
        this.year = pr.getYear();
    }
    public ScoreFileDTO(){
        
    }
}
