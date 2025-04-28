/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.icfes_group.service;

import com.icfes_group.model.Persona;
import com.icfes_group.model.Rol;
import com.icfes_group.repository.PersonaRepository;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author juanc
 */
@Service
public class AdminPersonService{
    @Autowired
    private PersonaRepository personaRepository;
    @Autowired
    private RolService rolService;
    public Persona changeRol(UUID personId, Integer nuevoRolId) {
    // Buscar la persona en la base de datos usando el ID

    Persona persona = personaRepository.findById(personId)
            .orElseThrow(() -> new RuntimeException("Persona no encontrada"));

    // Buscar el nuevo rol usando el rol ID
    Rol nuevoRol = rolService.findById(nuevoRolId);
    if (nuevoRol == null) {
        throw new RuntimeException("Rol no encontrado");
    }
    persona.setRol(nuevoRol);

    return personaRepository.save(persona);
}

}
