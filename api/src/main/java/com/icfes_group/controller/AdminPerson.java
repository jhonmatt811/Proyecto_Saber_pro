/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.icfes_group.controller;
import com.icfes_group.dto.ChangeRolDTO;
import com.icfes_group.model.User;
import com.icfes_group.service.AdminPersonService;
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
    @PutMapping("/change-rol")
    public User changeRol(@Valid @RequestBody ChangeRolDTO dto){
        return adminService.changeRol(dto.getUserId(), dto.getNuevoRolId());
    }
    //@PutMapping("/user/status")
    //public User changeStatus(){
        
    //}
}
