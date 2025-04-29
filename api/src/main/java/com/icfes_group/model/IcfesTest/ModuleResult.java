package com.icfes_group.model.IcfesTest;

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
    private GlobalResult resultadoGlobal;

    @ManyToOne
    @JoinColumn(name = "catalogo_modulo_id")
    private ModuleCatalog catalogoModulo;

    @Column(name = "puntaje_modulo")
    private Double puntajeModulo;

    @Column(name = "nivel_desempeno")
    private Double nivelDesempeno;

    @Column(name = "percentil_nacional_modulo")
    private Double percentilNacionalModulo;

    @Column(name = "percentil_grupo_referencia_modulo")
    private Double percentilGrupoReferenciaModulo;

    private String novedades;
}
