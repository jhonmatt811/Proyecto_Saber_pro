package com.icfes_group.service;

import com.icfes_group.dto.ComparativeIcfesPersonDTO;
import com.icfes_group.repository.ComparativeIcfesRepository;
import com.icfes_group.repository.proyections.ComparativeIcfesProjectionGroup;
import com.icfes_group.repository.proyections.ComparativeIcfesProjectionPerson;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class ComparativeIcfesService {
    private  final ComparativeIcfesRepository comparativeIcfesRepository;

    public ComparativeIcfesPersonDTO comparePerson(Integer documento, Integer year,Long programa, Long grupo) throws Exception{
         List<ComparativeIcfesProjectionPerson> person = comparativeIcfesRepository.getResumenEstudiante(documento,year);
         if(person.isEmpty()){
             throw new Exception("No hay resultados para el estudiante");
         }
         List<ComparativeIcfesProjectionGroup> group = comparativeIcfesRepository.getComparativeIcfes(documento,year,programa,grupo);
         if(group.isEmpty()){
             throw new Exception("No hay resultados para el grupo");
         }
         ComparativeIcfesPersonDTO dto = new ComparativeIcfesPersonDTO();
         dto.setNombreEstudiante(person.getFirst().getNombreEvaluado());
        List<Map<String, Object>> resultadosEstudiante = person.stream()
                .map(p -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("nombreModulo", p.getNombreModulo());
                    map.put("puntajeModulo", p.getPuntajeModulo());
                    return map;
                }).toList();

        List<Map<String, Object>> resultadosGrupo = group.stream()
                .map(g -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("promedioGrupoGlobal", g.getPromedioGrupoGlobal());
                    map.put("promedioGrupoModulo", g.getPromedioGrupoModulo());
                    map.put("varianzaGlobal", g.getVarianzaGlobal());
                    map.put("varianzaModulo", g.getVarianzaModulo());
                    return map;
                }).toList();
        dto.setResultadosEstudiante(resultadosEstudiante);
        dto.setResultadosGrupo(resultadosGrupo);
        dto.setTotalEstudiante(person.getFirst().getPuntajeGlobal());
        return dto;
    }
}
