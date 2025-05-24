/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.icfes_group.controller.Admin;

import com.icfes_group.controller.responses.StatusResponse;
import com.icfes_group.dto.PersonaDTO;
import com.icfes_group.model.Persona;
import com.icfes_group.service.PersonService;
import com.icfes_group.service.UserService;
import com.icfes_group.service.admin.AdminPersonService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author juanc
 */
@RestController
@RequestMapping("/admin/personas")
@AllArgsConstructor
public class AdminPersonController {
    private AdminPersonService personaService;
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
    @PostMapping("/lote")
    public ResponseEntity<?> createPersonLote(@Valid @RequestBody PersonaDTO[] personaDTO) {
        try {
            Persona[] personas = personaService.guardarPersonaLote(personaDTO);
            return new ResponseEntity<>(personas, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(new StatusResponse("Bad", e.toString()), HttpStatus.BAD_REQUEST);
        }
    }
}
