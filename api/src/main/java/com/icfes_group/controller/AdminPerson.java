/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.icfes_group.controller;
import com.icfes_group.controller.PersonController;
import com.icfes_group.dto.PersonaDTO;
import com.icfes_group.model.Persona;
import com.icfes_group.model.Rol;
import com.icfes_group.service.AdminPersonService;
import java.util.UUID;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
/**
 *
 * @author juanc
 */
@RestController
@RequestMapping("/personas/admin")
public class AdminPerson extends PersonController{
    @Autowired
    private AdminPersonService adminService;
    public Persona changeRol(@Valid @RequestBody UUID personId,Integer nuevoRolId){
        return adminService.changeRol(personId, nuevoRolId);
    }
}
