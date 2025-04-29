package com.icfes_group.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.icfes_group.service.UserService;
import com.icfes_group.dto.PersonaDTO;
import com.icfes_group.dto.UserDTO;
import com.icfes_group.model.User;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/usuarios")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping 
    public ResponseEntity<?> createUser(@Valid @RequestBody UserDTO dto) {
        try {
            String passwd = userService.generateDefPasswd();
            String hashPasswd = userService.hashPasswd(passwd);
            User user = userService.register(dto, hashPasswd);
            userService.sendEmail(dto.getPerson(), passwd);
            return new ResponseEntity<>(new StatusResponse("OK","Usuario Creado"), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(new StatusResponse("Bad", e.toString()), HttpStatus.BAD_REQUEST);
        }
    }
    @PostMapping("/inicio-sesion")
    public ResponseEntity<?> login(@Valid @RequestBody UserDTO dto){
        try {
            User user = userService.login(dto);
            return new ResponseEntity<>(new StatusResponse("Ok","Inicio Sesion correctamente "+dto.getEmail()),HttpStatus.ACCEPTED);
        } catch (Exception e) {
            return new ResponseEntity<>(new StatusResponse("BAD",e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
}
