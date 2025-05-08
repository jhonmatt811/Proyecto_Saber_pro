/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.icfes_group.controller;

import com.icfes_group.controller.responses.StatusResponse;
import com.icfes_group.model.IcfesTest.AcademicProgram;
import com.icfes_group.service.AcademicProgramsService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author juanc
 */
@RestController
@RequestMapping("/programas")
public class AcademicPrograms {
    @Autowired
    AcademicProgramsService academicProgramService;
    @GetMapping
    public ResponseEntity<?> getPrograms(){
        try {
            List<AcademicProgram> listPrograms = academicProgramService.getPrograms();
            return new ResponseEntity<>(listPrograms.toArray(),HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new StatusResponse("BAD",e.getMessage()),HttpStatus.BAD_REQUEST);
        }
    }
}
