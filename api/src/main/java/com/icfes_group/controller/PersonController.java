/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.icfes_group.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.icfes_group.service.PersonaService;
import com.icfes_group.model.Persona;
import com.icfes_group.dto.PersonaDTO;
import com.icfes_group.service.RolService;
import jakarta.validation.Valid;
/**
 *
 * @author juanc
 */
@RestController
@RequestMapping("/personas")
public class PersonController {
    @Autowired
    private PersonaService personaService;
    @Autowired
    private RolService rolService;
    @PostMapping
    public Persona createPerson(@Valid @RequestBody PersonaDTO personaDTO){
        return personaService.guardarPersona(personaDTO);
    }
}
