package com.icfes_group.controller;

import com.icfes_group.controller.responses.StatusResponse;
import com.icfes_group.service.ComparativeIcfesService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/comparar/persona/{id}")
@AllArgsConstructor
public class ComparativeIcfesController {
    private final ComparativeIcfesService comparativeIcfesService;
    @GetMapping()
    public ResponseEntity<?> comparePerson(
            @RequestParam(required = false) Integer documento,
            @RequestParam(required = false) Long programa,
            @RequestParam(required = false) Long grupo,
            @RequestParam(required = true)  Integer year
    ){
        try{
            if(documento != null){
                return new ResponseEntity<>(comparativeIcfesService.comparePerson(documento,year,programa,grupo), HttpStatus.OK);
            }else{
                return new ResponseEntity<>(comparativeIcfesService.compareGroup(programa), HttpStatus.OK);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(new StatusResponse("BAD",e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

}
