package com.icfes_group.model.IcfesTest;

import com.icfes_group.dto.ScoreFileDTO;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.util.UUID;
import lombok.Data;

/**
 *
 * @author juanc
 */
@Data
@Entity
@Table(name = "registros_evaluaciones")
public class TestRegistration {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Aquí solo almacenamos los identificadores en lugar de las instancias completas
    @Column(name = "evaluado_id")
    private Long evaluadoId;  // En lugar de Evaluated evaluado

    @Column(name = "programa_id")
    private Long programaId;  // En lugar de AcademicProgram programa

    @Column(name = "tipo_evaluado_id")
    private Long tipoEvaluadoId;  // En lugar de EvaluatedType tipoEvaluado

    @Column(name = "ciudad_id")
    private Long ciudadId;  // En lugar de City ciudad

    @Column(name = "numero_registro")
    private String numeroRegistro;

    private Integer ciclo;
    private Integer year;



    // Constructor simplificado que recibe los valores directamente
    public TestRegistration(ScoreFileDTO dto, Long programaId, Long tipoEvaluadoId, Long ciudadId) {
        this.numeroRegistro = dto.getNumeroRegistro();
        this.ciclo = dto.getCiclo();
        this.year = dto.getYear();
        this.evaluadoId = dto.getDocumento();
        this.programaId = programaId;
        this.tipoEvaluadoId = tipoEvaluadoId;
        this.ciudadId = ciudadId;
    }

    // Constructor vacío
    public TestRegistration() {}

}
