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

    @PutMapping("/{id}")
    public ResponseEntity<?> getSuggestActions(@PathVariable UUID id, @RequestBody ImprovementActionsDTO dto){
        try {
            ImprovementActions improvementActions = improvementActionsService.updateActionsImprovements(id, dto);
            return new ResponseEntity<>(improvementActions,HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new StatusResponse("BAD",e.getMessage()),HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteImprovementAction(@PathVariable UUID id){
        try {
            improvementActionsService.deleteActionsImprovements(id);
            return new ResponseEntity<>(new StatusResponse("OK","Se ha eliminado la mejora"),HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new StatusResponse("BAD",e.getMessage()),HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/sugerencias/{id}")
    public ResponseEntity<?> getSuggest(
            @PathVariable UUID id,
            @RequestParam(required = true) Integer yearInicio,
            @RequestParam(required = true) Integer yearFin

    ){
        try {
            return new ResponseEntity<>(improvementActionsService.analyze(id,yearInicio,yearFin),HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new StatusResponse("BAD",e.getMessage()),HttpStatus.BAD_REQUEST);
        }
    }
}
