package com.icfes_group.service;

import com.icfes_group.dto.ImprovementActionsAnalyzeDTO;
import com.icfes_group.dto.ImprovementActionsDTO;
import com.icfes_group.integrate.gemini.service.GeminiIntegrationService;
import com.icfes_group.model.IcfesTest.ImprovementActions;
import com.icfes_group.repository.IcfesTestRepository.ImprovementActionsRepository;
import com.icfes_group.repository.proyections.ImprovementActionProjection;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ImprovementActionsService {
    private final ImprovementActionsRepository improvementActionsRepository;
    private final GeminiIntegrationService geminiIntegrationService;

    private boolean verifyActionTime(ImprovementActions improvementAction){
        Integer yearInicio = improvementAction.getYearInicio();
        Integer yearActual = LocalDateTime.now().getYear();
        return yearActual - yearInicio > 1;
    }

    public ImprovementActions addActionsImprovements(ImprovementActionsDTO dto){
        ImprovementActions improvementActions = new ImprovementActions(dto);
        return improvementActionsRepository.save(improvementActions);
    }

    public List<ImprovementActions> findAll() throws Exception{
        List<ImprovementActions> actions =  improvementActionsRepository.findAll();
        if(actions.isEmpty()){
            throw new Exception("No hay aportes registrados");
        }
        return actions;
    }

    public ImprovementActions updateActionsImprovements(UUID id,ImprovementActionsDTO dto) throws  Exception {
        Optional<ImprovementActions> optionalAction = improvementActionsRepository.findById(id);
        if (optionalAction.isEmpty()) {
            throw new Exception("No se encontró el aporte");
        }
        ImprovementActions improvementActions = optionalAction.get();
        if(verifyActionTime(improvementActions)){
            throw new Exception("El aporte no puede ser modificado porque tiene mas de un año de vigencia");
        }
        improvementActions.setYearInicio(dto.getYearInicio());
        improvementActions.setYearFin(dto.getYearFin());
        improvementActions.setPrograma(dto.getPrograma());
        improvementActions.setModulo(dto.getModulo());
        improvementActions.setSugerenciaMejora(dto.getSugerenciaMejora());
        return improvementActionsRepository.save(improvementActions);
    }

    public ImprovementActionsAnalyzeDTO analyze(UUID id) throws Exception{
        ImprovementActions improvementActions = improvementActionsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Accion de Mejora no encontrada"));

        Optional<ImprovementActionProjection> analyzeAction = improvementActionsRepository.analyzer(
                id,
                improvementActions.getYearInicio(),
                improvementActions.getYearFin()
        );
        if(analyzeAction.isEmpty()){
            throw new Exception("No hay aportes registrados");
        }
        Double difference = analyzeAction.get().getPromedioFinal() - analyzeAction.get().getPromedioInicio();
        Double percentageImprovement = (difference / analyzeAction.get().getPromedioInicio()) * 100;
        ImprovementActionsAnalyzeDTO analyzeAI = new ImprovementActionsAnalyzeDTO();
        analyzeAI.setPorcentajeMejora(percentageImprovement);
        analyzeAI.setAccionMejora(improvementActions);
        String responseAi = geminiIntegrationService.getGeminiData(analyzeAI);
        analyzeAI.setMessage(responseAi);
        return analyzeAI;
    }

    public void deleteActionsImprovements(UUID id) throws Exception{
        Optional<ImprovementActions> optionalAction = improvementActionsRepository.findById(id);
        if (optionalAction.isEmpty()) {
            throw new Exception("No se encontró el aporte");
        } else if (verifyActionTime(optionalAction.get())) {
            throw new Exception("El aporte no puede ser eliminado porque tiene mas de un año de vigencia");
        }
        improvementActionsRepository.deleteById(id);
    }
}
