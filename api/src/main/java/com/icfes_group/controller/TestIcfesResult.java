/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.icfes_group.controller;

import com.icfes_group.controller.responses.StatusResponse;
import com.icfes_group.dto.ScoreFileDTO;
import com.icfes_group.service.TestIcfesResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author juanc
 */
@RestController
@RequestMapping("/resultados")
public class TestIcfesResult {
    @Autowired
    TestIcfesResultService testResultService;
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
}
