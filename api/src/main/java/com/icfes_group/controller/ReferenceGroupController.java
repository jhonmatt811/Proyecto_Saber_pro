package com.icfes_group.controller;

import com.icfes_group.controller.responses.StatusResponse;
import com.icfes_group.security.ReferenceGroupService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/grupos")
@AllArgsConstructor
public class ReferenceGroupController {
    private final ReferenceGroupService groupsReferencesService;
    @GetMapping
    public ResponseEntity<?> getGroups(){
        try{
            return new ResponseEntity<>(groupsReferencesService.getAllGroups(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new StatusResponse("Bad", e.toString()), HttpStatus.BAD_REQUEST);
        }
    }
}
