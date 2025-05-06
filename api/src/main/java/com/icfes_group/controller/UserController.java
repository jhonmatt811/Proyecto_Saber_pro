package com.icfes_group.controller;

import com.icfes_group.controller.responses.LoginResponse;
import com.icfes_group.controller.responses.StatusResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.icfes_group.service.UserService;
import com.icfes_group.dto.PersonaDTO;
import com.icfes_group.dto.UserDTO;
import com.icfes_group.model.User;
import com.icfes_group.security.componets.JwtUtil;
import jakarta.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/usuarios")
public class UserController {
     
    @Autowired
    private JwtUtil jwtUtil;
    
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
            String token = jwtUtil.generateToken(dto.getEmail(),user.getRol().getNombre());
            return new ResponseEntity<>(new LoginResponse("OK","Inicio de Sesion Correcto",token),HttpStatus.ACCEPTED);
        } catch (Exception e) {
            return new ResponseEntity<>(new StatusResponse("BAD",e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
    @PutMapping("/active")
    public ResponseEntity<?> changeActive(@RequestParam Boolean activate, @RequestParam UUID id){
        try {
           System.out.println("Activando usuario con ID: " + id + ", activar: " + activate);
           User user = userService.changeActivate(activate, id);
           return new ResponseEntity<>(new StatusResponse("OK","Usuario Desactivado"),HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new StatusResponse("BAD",e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
}
