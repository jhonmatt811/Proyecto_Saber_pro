/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.icfes_group.controller;

import com.icfes_group.controller.responses.StatusResponse;
import com.icfes_group.dto.ScoreFileDTO;
import com.icfes_group.integrate.icfes.dto.IcfesIntegrationDTO;
import com.icfes_group.service.IcfesIntegrationService;
import com.icfes_group.service.TestIcfesResultService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 *
 * @author juanc
 */
@RestController
@RequestMapping("/resultados")
@AllArgsConstructor
public class TestIcfesResult {
    TestIcfesResultService testResultService;
    IcfesIntegrationService icfesIntegrationClient;

    @GetMapping
    public ResponseEntity<?>  getTestResult(
        @RequestParam(required = false) Integer year,
        @RequestParam(required = false) Integer ciclo,
        @RequestParam(required = false) Long programa,
        @RequestParam(required = false) Long documento
    ){
        try {
            ScoreFileDTO[] response = testResultService.getTestResults(year, ciclo, programa, documento);
            return new ResponseEntity<>(response,HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new StatusResponse("BAD",e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping("/icfes")
    public ResponseEntity<?> getTestIcfesResult(
            @RequestParam(required = false) Integer periodo,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) Long offset
    ) {
        try {
            Map<String, String> params = new HashMap<>();

            if (periodo != null) {
                params.put("periodo", periodo.toString());
            }
            if (limit != null) {
                params.put("$limit", limit.toString());
            }
            if (offset != null) {

                params.put("$offset", offset.toString()); // $offset es como lo espera la API de datos.gov.co
            }

            List<IcfesIntegrationDTO> result = icfesIntegrationClient.getIcfesData(params);
            return new ResponseEntity<>(result, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(new StatusResponse("BAD", e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
}
