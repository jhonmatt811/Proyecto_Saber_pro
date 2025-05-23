package com.icfes_group.repository.IcfesTestRepository;

import com.icfes_group.model.IcfesTest.ImprovementActions;
import com.icfes_group.repository.proyections.ImprovementActionProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ImprovementActionsRepository extends JpaRepository<ImprovementActions, UUID> {
        @Query(value = """
    SELECT 
        cm.nombre AS nombre,
        AVG(CASE WHEN re.year = :yearInicio THEN
            CAST(NULLIF(regexp_replace(rm.puntaje_modulo, '[^0-9]', '', 'g'), '') AS INTEGER)
            ELSE 0 
        END) AS promedioInicio,
        AVG(CASE WHEN re.year = :yearFin THEN
            CAST(NULLIF(regexp_replace(rm.puntaje_modulo, '[^0-9]', '', 'g'), '') AS INTEGER)
            ELSE 0
        END) AS promedioFinal
    FROM acciones_mejoras am
    JOIN resultados_modulos rm ON am.module_id = rm.catalogo_modulo_id
    JOIN catalogos_modulos cm ON rm.catalogo_modulo_id = cm.id
    JOIN programas_academicos pa ON am.programa_id = pa.id
    JOIN registros_evaluaciones re ON re.programa_id = pa.id
    WHERE am.id = :improvementActionId AND re.year IN (:yearInicio, :yearFin)
    GROUP BY cm.nombre
""", nativeQuery = true)
        Optional<ImprovementActionProjection> analyzer(
                @Param("improvementActionId") UUID improvementActionId,
                @Param("yearInicio") Integer yearInicio,
                @Param("yearFin") Integer yearFin
        );

}
