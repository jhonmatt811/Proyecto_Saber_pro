/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.icfes_group.service;

import com.icfes_group.model.Rol;
import com.icfes_group.model.User;
import com.icfes_group.repository.UserRepository;
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
    private UserRepository userRepository;
    @Autowired
    private RolService rolService;
    public User changeRol(UUID userId, Integer nuevoRolId) {
    // Buscar la persona en la base de datos usando el ID

       User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Persona no encontrada"));

    // Buscar el nuevo rol usando el rol ID
    Rol nuevoRol = rolService.findById(nuevoRolId);
    if (nuevoRol == null) {
        throw new RuntimeException("Rol no encontrado");
    }
    user.setRol(nuevoRol);

    return userRepository.save(user);
}

}
