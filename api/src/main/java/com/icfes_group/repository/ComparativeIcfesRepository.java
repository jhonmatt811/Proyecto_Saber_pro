package com.icfes_group.repository;

import com.icfes_group.repository.proyections.ComparativeIcfesProjectionGroup;
import com.icfes_group.repository.proyections.ComparativeIcfesProjectionPerson;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Entity
class RootEntityComparative { @Id
Integer id; }
@Repository
public interface ComparativeIcfesRepository extends JpaRepository<RootEntityComparative, UUID> {

    @Query(value = """
        SELECT
            ROUND(AVG(CASE 
                WHEN re.evaluado_id != :documento 
                THEN CAST(rg.puntaje_global AS INTEGER) 
                ELSE NULL 
            END),2) AS promedioGrupoGlobal,

            ROUND(AVG(CASE 
                WHEN re.evaluado_id != :documento 
                THEN CAST(rm.puntaje_modulo AS INTEGER) 
                ELSE NULL 
            END),2) AS promedioGrupoModulo,

            ROUND(VAR_SAMP(CASE 
                WHEN re.evaluado_id != :documento 
                THEN CAST(rg.puntaje_global AS INTEGER) 
                ELSE NULL 
            END),2) AS varianzaGlobal,

            ROUND(VAR_SAMP(CASE 
                WHEN re.evaluado_id != :documento 
                THEN CAST(rm.puntaje_modulo AS INTEGER) 
                ELSE NULL 
            END),2) AS varianzaModulo

        FROM registros_evaluaciones re
        JOIN resultados_globales rg ON rg.id = re.id
        JOIN resultados_modulos rm ON rm.resultado_global_id = rg.id
        JOIN catalogos_modulos cm ON cm.id = rm.catalogo_modulo_id        
        WHERE re.year = :year
            AND (:programa IS NULL OR re.programa_id = :programa)
            AND (:grupo IS NULL OR rg.grupo_referencia_id = :grupo)
        GROUP BY cm.nombre
    """, nativeQuery = true)
    List<ComparativeIcfesProjectionGroup> getComparativeIcfes(
            @Param("documento") Integer documento,
            @Param("year") Integer year,
            @Param("programa") Long programa,
            @Param("grupo") Long grupo
    );

        @Query(value = """
             SELECT
                 e.nombre AS nombreEvaluado,
                 cm.nombre AS nombreModulo,
                 ROUND(CAST(rg.puntaje_global AS NUMERIC), 2) AS puntajeGlobal,
                 ROUND(CAST(rm.puntaje_modulo AS NUMERIC), 2) AS puntajeModulo
             FROM registros_evaluaciones re
             JOIN evaluados e ON e.documento = re.evaluado_id
             JOIN resultados_globales rg ON rg.id = re.id
             JOIN resultados_modulos rm ON rm.resultado_global_id = rg.id
             JOIN catalogos_modulos cm ON cm.id = rm.catalogo_modulo_id
            WHERE 
                re.evaluado_id = :documento
                AND re.year = :year
            """,nativeQuery = true)
        List<ComparativeIcfesProjectionPerson> getResumenEstudiante(@Param("documento") Integer documento, @Param("year") Integer year);
}
