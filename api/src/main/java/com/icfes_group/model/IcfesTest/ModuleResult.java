package com.icfes_group.model.IcfesTest;

import com.icfes_group.dto.ScoreFileDTO;
import jakarta.persistence.*;
import java.util.UUID;
import lombok.Data;

@Data
@Entity
@Table(name = "resultados_modulos")
public class ModuleResult {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "resultado_global_id")
    private GlobalResult globalResult;

    @ManyToOne
    @JoinColumn(name = "catalogo_modulo_id")
    private ModuleCatalog moduleCatalog;

    @Column(name = "puntaje_modulo")
    private String puntajeModulo;

    @Column(name = "nivel_desempeno")
    private String nivelDesempeño;

    @Column(name = "percentil_nacional_modulo")
    private String percentilNacionalModulo;

    @Column(name = "percentil_grupo_referencia_modulo")
    private String percentilGrupoReferenciaModulo;


    public ModuleResult(ScoreFileDTO dto,GlobalResult globalResult,ModuleCatalog moduleCatalog){
        this.globalResult = globalResult;
        this.puntajeModulo = dto.getPuntajeModulo();
        this.nivelDesempeño = dto.getNivelDesempeño();
        this.moduleCatalog = moduleCatalog;
        this.percentilGrupoReferenciaModulo = dto.getPercentilGrupoNbcModulo();
        this.percentilNacionalModulo = dto.getPercentilNacionalModulo();
    }
    
    public ModuleResult(){}
}
