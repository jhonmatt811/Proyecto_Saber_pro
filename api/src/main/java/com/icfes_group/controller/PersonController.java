package com.icfes_group.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.icfes_group.service.PersonaService;
import com.icfes_group.model.Persona;
import com.icfes_group.service.UserService;
import com.icfes_group.dto.PersonaDTO;
import com.icfes_group.model.User;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/personas")
public class PersonController {

    @Autowired
    private PersonaService personaService;

    @Autowired
    private UserService userService;

    @PostMapping 
    public ResponseEntity<?> createPerson(@Valid @RequestBody PersonaDTO personaDTO) {
        try {
            Persona persona = personaService.guardarPersona(personaDTO);
            return new ResponseEntity<>(persona, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(new StatusResponse("Bad", e.toString()), HttpStatus.BAD_REQUEST);
        }
    }
}
