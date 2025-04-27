/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.icfes_group.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;
import com.icfes_group.repository.RolRepository;
import com.icfes_group.model.Rol;
/**
 *
 * @author juanc
 */
@Service
public class RolService {

    private final RolRepository rolRepository;

    // Inyección de dependencias del RolRepository
    @Autowired
    public RolService(RolRepository rolRepository) {
        this.rolRepository = rolRepository;
    }

    // Método para encontrar un rol por su ID
    public Rol findById(Integer id) {
        Optional<Rol> rol = rolRepository.findById(id);
        if (rol.isPresent()) {
            return rol.get();
        } else {
            // Maneja el caso cuando no se encuentra el rol (puedes lanzar una excepción si es necesario)
            throw new RuntimeException("Rol no encontrado con ID: " + id);
        }
    }

}