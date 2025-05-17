/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.icfes_group.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import com.icfes_group.repository.RolRepository;
import com.icfes_group.model.Rol;
/**
 *
 * @author juanc
 */
@Service
public class RolService {
    @Autowired
    private RolRepository rolRepository;

    // Método para encontrar un rol por su ID
    public Rol findById(Integer id) throws Exception{
        Optional<Rol> rol = rolRepository.findById(id);
        if (rol.isPresent()) {
            return rol.get();
        } else {
            // Maneja el caso cuando no se encuentra el rol (puedes lanzar una excepción si es necesario)
            throw new Exception("Rol no encontrado con ID: " + id);
        }
    }

    public List<Rol> findAll() throws Exception{
        List<Rol> roles = rolRepository.findAll();
        if (roles.isEmpty()) {
            throw new Exception("No se encontraron roles");
        }
        return roles;
    }

}