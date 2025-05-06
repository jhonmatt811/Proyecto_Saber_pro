package com.icfes_group.service;

import com.icfes_group.dto.ScoreFileDTO;
import com.icfes_group.repository.TestIcfesResultRepository;
import com.icfes_group.repository.proyections.ScoreFileProjection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TestIcfesResultService {

    @Autowired
    private TestIcfesResultRepository testIcfesResultRepository;

    public ScoreFileDTO[] getTestResults(Integer year, Integer ciclo, Long programa, Long documento) throws Exception {
        List<ScoreFileProjection> testResults = testIcfesResultRepository.getReporteDTO(year, ciclo, programa, documento);
        if (testResults.isEmpty()) {
            throw new Exception("No hay resultados ingresados aún");
        }

        // Mapea los resultados a DTOs
        List<ScoreFileDTO> scoreFileDTOs = testResults.stream()
                .map(result -> new ScoreFileDTO(result))  // Usa el constructor adecuado aquí
                .collect(Collectors.toList());

        return scoreFileDTOs.toArray(new ScoreFileDTO[0]);
    }
}
