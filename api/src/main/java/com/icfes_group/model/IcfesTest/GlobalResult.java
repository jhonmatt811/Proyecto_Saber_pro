package com.icfes_group.model.IcfesTest;

import com.icfes_group.dto.ScoreFileDTO;
import jakarta.persistence.*;
import java.util.UUID;
import lombok.Data;

@Data
@Entity
@Table(name = "resultados_globales")
public class GlobalResult {
     @Id
    private String id; // Necesario para que Hibernate sepa cuál es el ID

    @OneToOne
    @MapsId // Usa el valor de registroEvaluacion.id como PK y FK
    @JoinColumn(name = "id")
    private TestRegistration registroEvaluacion;

    @ManyToOne
    @JoinColumn(name = "grupo_referencia_id")
    private ReferenceGroup grupoReferencia;

    @Column(name = "puntaje_global")
    private String puntajeGlobal;

    @Column(name = "percentil_nacional_global")
    private String percentilNacionalGlobal;

    @Column(name = "percentil_grupo_referencia")
    private String percentilGrupoReferencia;

    private String novedades;
    
    public GlobalResult(ScoreFileDTO dto,TestRegistration test,ReferenceGroup groupReference){
        this.registroEvaluacion = test;
        this.puntajeGlobal = dto.getPuntajeGlobal();
        this.percentilNacionalGlobal = dto.getPercentilNacionalGlobal();
        this.percentilGrupoReferencia = dto.getPercentilNacionalNbc();
        this.grupoReferencia = groupReference;
        this.novedades = dto.getNovedades();
    }
    
    public GlobalResult(){}
}
