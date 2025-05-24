package com.icfes_group.model.IcfesTest;

import com.icfes_group.dto.ImprovementActionsDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "acciones_mejoras")
@NoArgsConstructor
@Getter
@Setter
public class ImprovementActions {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "programa_id")
    private AcademicProgram programa;
    @ManyToOne
    @JoinColumn(name = "module_id")
    private ModuleCatalog modulo;
    @Column(nullable = false, name = "propuesta_mejora")
    private String sugerenciaMejora;

    private Integer yearInicio;
    private Integer yearFin;

    public ImprovementActions(ImprovementActionsDTO dto) {
        this.programa = dto.getPrograma();
        this.modulo = dto.getModulo();
        this.sugerenciaMejora = dto.getSugerenciaMejora();
        this.yearInicio = dto.getYearInicio();
        this.yearFin = dto.getYearFin();
    }
}
