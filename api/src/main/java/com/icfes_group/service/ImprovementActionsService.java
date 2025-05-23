package com.icfes_group.service;

import com.icfes_group.dto.ImprovementActionsAnalyzeDTO;
import com.icfes_group.dto.ImprovementActionsDTO;
import com.icfes_group.integrate.gemini.service.GeminiIntegrationService;
import com.icfes_group.model.IcfesTest.ImprovementActions;
import com.icfes_group.repository.IcfesTestRepository.ImprovementActionsRepository;
import com.icfes_group.repository.proyections.ImprovementActionProjection;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ImprovementActionsService {
    private final ImprovementActionsRepository improvementActionsRepository;
    private final GeminiIntegrationService geminiIntegrationService;

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

    public ImprovementActionsAnalyzeDTO analyze(ImprovementActionsAnalyzeDTO dto) throws Exception{
        ImprovementActions improvementActions = dto.getAccionMejora();
        Optional<ImprovementActionProjection> analyzeAction = improvementActionsRepository.analyzer(
                improvementActions.getId(),
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
        analyzeAI.setAccionMejora(dto.getAccionMejora());
        String responseAi = geminiIntegrationService.getGeminiData(analyzeAI);
        analyzeAI.setMessage(responseAi);
        return analyzeAI;
    }
}
