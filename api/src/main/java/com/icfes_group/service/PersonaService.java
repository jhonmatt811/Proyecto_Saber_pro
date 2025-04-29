package com.icfes_group.service;

import com.icfes_group.dto.PersonaDTO;
import com.icfes_group.model.Persona;
import com.icfes_group.model.Rol;
import com.icfes_group.repository.PersonaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PersonaService {

    private final PersonaRepository personaRepository;
    private final RolService rolService;

    @Autowired
    public PersonaService(PersonaRepository personaRepository, RolService rolService) {
        this.personaRepository = personaRepository;
        this.rolService = rolService;
    }

    public Persona guardarPersona(PersonaDTO dto) {
        Persona persona = new Persona(
            dto
        );        
        return personaRepository.save(persona);
    }
}
