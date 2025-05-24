/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.icfes_group.service.admin;

import com.icfes_group.dto.PersonaDTO;
import com.icfes_group.model.Persona;
import com.icfes_group.repository.PersonaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author juanc
 */
@Service
public class AdminPersonService {
    private final PersonaRepository personaRepository;

    @Autowired
    public AdminPersonService(PersonaRepository personaRepository) {
        this.personaRepository = personaRepository;
    }

    public Persona guardarPersona(PersonaDTO dto) {
        Persona persona = new Persona(
            dto
        );        
        return personaRepository.save(persona);
    }
    public Persona[] guardarPersonaLote(PersonaDTO[] personaDTO) {
        Persona[] personas = new Persona[personaDTO.length];
        for (int i = 0; i < personaDTO.length; i++) {
            personas[i] = guardarPersona(personaDTO[i]);
        }
        return personas;
    }
}
