package com.icfes_group.controller;

import com.icfes_group.controller.responses.StatusResponse;
import com.icfes_group.dto.ImprovementActionsAnalyzeDTO;
import com.icfes_group.dto.ImprovementActionsDTO;
import com.icfes_group.model.IcfesTest.ImprovementActions;
import com.icfes_group.service.ImprovementActionsService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/mejoras")
@AllArgsConstructor
public class ImprovementActionsController {
    ImprovementActionsService improvementActionsService;
    @PostMapping
    public ResponseEntity<?> createImprovementAction(@RequestBody ImprovementActionsDTO dto){
        try{
            ImprovementActions improvementActions = improvementActionsService.addActionsImprovements(dto);
            return new ResponseEntity<>(improvementActions, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(new StatusResponse("BAD",e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping
    public ResponseEntity<?> getImprovementActions(){
        try {
            return new ResponseEntity<>(improvementActionsService.findAll(),HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new StatusResponse("BAD",e.getMessage()),HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/sugerencias")
    public ResponseEntity<?> getSuggest(@RequestBody ImprovementActionsAnalyzeDTO dto){
        try {
            return new ResponseEntity<>(improvementActionsService.analyze(dto),HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new StatusResponse("BAD",e.getMessage()),HttpStatus.BAD_REQUEST);
        }
    }
}
