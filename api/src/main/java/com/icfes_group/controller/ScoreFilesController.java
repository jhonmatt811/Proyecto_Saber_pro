/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.icfes_group.controller;

import com.icfes_group.dto.ScoreFileDTO;
import jakarta.validation.Valid;
import com.icfes_group.service.ScoreFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
/**
 *
 * @author juanc
 */
@RestController
@RequestMapping("/resultados/file")
public class ScoreFilesController {
    @Autowired
    private ScoreFileService scoreFileService;
    @PostMapping    
    public ScoreFileDTO[] saveDataFile(@Valid @RequestBody ScoreFileDTO[] dto){
        // Depuracion Imprimir cuántos resultados llegan desde el cliente
        ScoreFileDTO[] score = scoreFileService.saveDataFile(dto);
        System.out.println(">>> Backend recibió " + dto.length + " resultados");
        return score;
    }
}
