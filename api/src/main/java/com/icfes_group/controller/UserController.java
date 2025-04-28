package com.icfes_group.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.icfes_group.service.UserService;
import com.icfes_group.dto.PersonaDTO;
import com.icfes_group.model.User;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/usuarios")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping 
    public ResponseEntity<?> createUser(@Valid @RequestBody PersonaDTO personaDTO) {
        try {
            String passwd = userService.generateDefPasswd();
            String hashPasswd = userService.hashPasswd(passwd);
            User user = userService.register(personaDTO, hashPasswd);
            userService.sendEmail(personaDTO, passwd);
            return new ResponseEntity<>(new StatusResponse("OK","Usuario Creado"), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(new StatusResponse("Bad", e.toString()), HttpStatus.BAD_REQUEST);
        }
    }
}
