package com.icfes_group.service;

import com.icfes_group.dto.ComparativeIcfesGroupDTO;
import com.icfes_group.dto.ComparativeIcfesPersonDTO;
import com.icfes_group.repository.ComparativeIcfesRepository;
import com.icfes_group.repository.proyections.ComparativeIcfesProjectionGroup;
import com.icfes_group.repository.proyections.ComparativeIcfesProjection;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class ComparativeIcfesService {
    private  final ComparativeIcfesRepository comparativeIcfesRepository;

    public ComparativeIcfesPersonDTO comparePerson(Integer documento, Integer year,Long programa, Long grupo) throws Exception{
        if(documento == null && programa == null){
            throw new Exception("Debe ingresar un documento o un programa");
        }
        List<ComparativeIcfesProjection> resAnalyze = comparativeIcfesRepository.getResumen(documento,year,programa);
         if(resAnalyze.isEmpty()){
             throw new Exception("No hay resultados para el estudiante");
         }
         List<ComparativeIcfesProjectionGroup> group = comparativeIcfesRepository.getComparativeIcfes(documento,year,programa,grupo);
         if(group.isEmpty()){
             throw new Exception("No hay resultados para el grupo");
         }
         if(group.getFirst().getPromedioGrupoModulo() == null){
            throw new Exception("No hay resultados para el programa");
         }
         ComparativeIcfesPersonDTO dto = new ComparativeIcfesPersonDTO();
         dto.setNombreEstudiante(resAnalyze.getFirst().getNombreEvaluado());
         dto.setNombrePrograma(resAnalyze.getFirst().getProgramaAcademico());
        List<Map<String, Object>> resultsAnalyze = resAnalyze.stream()
                .map(p -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("nombreModulo", p.getNombreModulo());
                    map.put("puntajeModulo", p.getPuntajeModulo());
                    return map;
                }).toList();

        List<Map<String, Object>> resultadosGrupo = group.stream()
                .map(g -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("nombreModulo", g.getNombreModulo());
                    map.put("programaAcademico", g.getProgramaAcademico());
                    map.put("nombreGrupoReferencia", g.getNombreGrupoReferencia());
                    map.put("promedioGrupoGlobal", g.getPromedioGrupoGlobal());
                    map.put("promedioGrupoModulo", g.getPromedioGrupoModulo());
                    map.put("varianzaGlobal", g.getVarianzaGlobal());
                    map.put("varianzaModulo", g.getVarianzaModulo());
                    return map;
                }).toList();
        dto.setResultados(resultsAnalyze);
        dto.setResultadosGrupo(resultadosGrupo);
        dto.setTotalEstudiante(resAnalyze.getFirst().getPuntajeGlobal());
        return dto;
    }

    public ComparativeIcfesPersonDTO compareGroup(Long programa) throws Exception{
        List<ComparativeIcfesProjectionGroup> mwGroup = comparativeIcfesRepository.getComparativeIcfesMeGroup(programa);
        List<ComparativeIcfesProjectionGroup> groups = comparativeIcfesRepository.getComparativeIcfesGroup(programa);
        if(mwGroup.isEmpty()){
            throw new Exception("No hay resultados para el programa");
        }
        ComparativeIcfesPersonDTO dto = new ComparativeIcfesPersonDTO();
        dto.setNombrePrograma(mwGroup.getFirst().getProgramaAcademico());
        List<Map<String, Object>> resultados = mwGroup.stream()
                .map(g -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("nombreModulo", g.getNombreModulo());
                    map.put("programaAcademico", g.getProgramaAcademico());
                    map.put("nombreGrupoReferencia", g.getNombreGrupoReferencia());
                    map.put("promedioGrupoGlobal", g.getPromedioGrupoGlobal());
                    map.put("promedioGrupoModulo", g.getPromedioGrupoModulo());
                    map.put("varianzaGlobal", g.getVarianzaGlobal());
                    map.put("varianzaModulo", g.getVarianzaModulo());
                    return map;
                }).toList();
        List<Map<String, Object>> resultadosGrupo = groups.stream()
                .map(g -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("nombreModulo", g.getNombreModulo());
                    map.put("programaAcademico", g.getProgramaAcademico());
                    map.put("nombreGrupoReferencia", g.getNombreGrupoReferencia());
                    map.put("promedioGrupoGlobal", g.getPromedioGrupoGlobal());
                    map.put("promedioGrupoModulo", g.getPromedioGrupoModulo());
                    map.put("varianzaGlobal", g.getVarianzaGlobal());
                    map.put("varianzaModulo", g.getVarianzaModulo());
                    return map;
                }).toList();

        dto.setResultados(resultados);
        dto.setResultadosGrupo(resultadosGrupo);
        dto.setNombrePrograma(mwGroup.getFirst().getProgramaAcademico());
        return dto;
    }
}
