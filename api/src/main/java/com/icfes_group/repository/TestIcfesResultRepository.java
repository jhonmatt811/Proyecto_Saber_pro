package com.icfes_group.repository;

import com.icfes_group.dto.ScoreFileDTO;
import com.icfes_group.repository.proyections.ScoreFileProjection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import org.springframework.stereotype.Repository;

@Entity class RootEntity { @Id Integer id; }  
@Repository
public interface TestIcfesResultRepository extends JpaRepository<RootEntity, Integer>{

        @Query(value = """
        SELECT 
            re.id AS "numeroRegistro",
            e.nombre AS "nombre",
            e.documento AS "documento",
            te.nombre AS "tipoEvaluado",
            td.nombre AS "tipoDocumento",
            p.nombre AS "programa",
            p.snies AS "sniesProgramaAcademico",
            c.nombre AS "ciudad",
            g.puntaje_global AS "puntajeGlobal",
            g.percentil_nacional_global AS "percentilNacionalGlobal",
            g.percentil_grupo_referencia AS "percentilNacionalNbc",
            gr.id AS "idNucleoBasicoConocimiento",
            gr.nombre AS "nucleoBasicoConocimiento",
            cm.nombre AS "modulo",
            rm.puntaje_modulo AS "puntajeModulo",
            rm.nivel_desempeno AS "nivelDesempeño",
            rm.percentil_nacional_modulo AS "percentilNacionalModulo",
            rm.percentil_grupo_referencia_modulo AS "percentilGrupoNbcModulo",
            rm.novedades AS "novedades"            
        FROM registros_evaluaciones re
        JOIN tipos_evaluados te ON te.id = re.tipo_evaluado_id
        JOIN evaluados e ON e.documento = re.evaluado_id
        JOIN tipos_documentos td ON e.tipo_documento_id = td.id
        JOIN programas_academicos p ON p.id = re.programa_id
        JOIN ciudades c ON c.id = re.ciudad_id
        JOIN resultados_globales g ON g.id = re.id
        JOIN grupos_referencias gr ON gr.id = g.grupo_referencia_id
        JOIN resultados_modulos rm ON rm.resultado_global_id = g.id
        JOIN catalogos_modulos cm ON cm.id = rm.catalogo_modulo_id
        """, nativeQuery = true)
    List<ScoreFileProjection> getReporteDTO();

}
