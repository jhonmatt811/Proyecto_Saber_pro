/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.icfes_group.controller.Admin;
import com.icfes_group.controller.responses.StatusResponse;
import com.icfes_group.dto.UserDTO;
import com.icfes_group.model.User;
import com.icfes_group.service.admin.AdminUserService;
import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
/**
 *
 * @author juanc
 */
@RestController
@RequestMapping("/admin/usuarios")
@AllArgsConstructor
public class AdminUserController{
    private AdminUserService adminService;

    @GetMapping
    public ResponseEntity<?> loadUserPeople(){
        try{
            List<User> response = adminService.loadUserPeople();
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new StatusResponse("Bad", e.toString()), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping 
    public ResponseEntity<?> createUser(@Valid @RequestBody UserDTO dto) {
        try {
            String passwd = adminService.generateDefPasswd();
            String hashPasswd = adminService.hashPasswd(passwd);
            adminService.register(dto, hashPasswd);
            adminService.sendEmail(dto.getPerson(), passwd);
            return new ResponseEntity<>(new StatusResponse("OK","Usuario Creado"), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(new StatusResponse("Bad", e.toString()), HttpStatus.BAD_REQUEST);
        }
    }
    @PostMapping("/lote")
    public ResponseEntity<?> createUserLote(@Valid @RequestBody UserDTO[] dto) {
        try {
            for(UserDTO user : dto){
                String passwd = adminService.generateDefPasswd();
                String hashPasswd = adminService.hashPasswd(passwd);
                adminService.register(user, hashPasswd);
                adminService.sendEmail(user.getPerson(), passwd);
            }
            return new ResponseEntity<>(new StatusResponse("OK","Usuarios Creados"), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(new StatusResponse("Bad", e.toString()), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}/active")
    public ResponseEntity<?> changeActive(@RequestParam Boolean activate, @PathVariable UUID id){
        try {
           adminService.changeActivate(activate, id);
           String message = activate? "Activado":"Desactivado";
           return new ResponseEntity<>(new StatusResponse("OK","Usuario " + message),HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new StatusResponse("BAD",e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
    
    @PutMapping("/{id}/rol")
    public ResponseEntity<?> changeRol(@PathVariable UUID id, @RequestParam Integer rolId){
        try {
            User user = adminService.changeRol(id, rolId);
            return new ResponseEntity<>(user,HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new StatusResponse("BAD",e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
}
