package com.icfes_group.controller;

import com.icfes_group.controller.responses.LoginResponse;
import com.icfes_group.controller.responses.StatusResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.icfes_group.service.UserService;
import com.icfes_group.dto.UserDTO;
import com.icfes_group.model.User;
import com.icfes_group.security.componets.JwtUtil;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/usuarios")
@AllArgsConstructor
public class UserController {
     
    private final JwtUtil jwtUtil;
    private final UserService userService;
    
    @PostMapping("/inicio-sesion")
    public ResponseEntity<?> login(@Valid @RequestBody UserDTO dto){
        try {
            User user = userService.login(dto);
            String token = jwtUtil.generateToken(dto.getEmail(),user.getRol().getNombre());
            System.out.println(user);
            if(userService.mustChangePassword(user)){
                return new ResponseEntity<>(new StatusResponse("WARN","¡Debe cambiar la contraseña!"),HttpStatus.FORBIDDEN);
            }
            return new ResponseEntity<>(new LoginResponse("OK","Inicio de Sesion Correcto",token),HttpStatus.ACCEPTED);
        } catch (Exception e) {
            return new ResponseEntity<>(new StatusResponse("BAD",e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/contraseña")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody UserDTO dto){
        try{
            userService.changePassword(dto);
            return new ResponseEntity<>(new StatusResponse("OK","Cambio de contraseña exitoso"),HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new StatusResponse("BAD",e.getMessage()),HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/contraseña/olvidado")
    public ResponseEntity<?> forgetPassword(@Valid @RequestBody UserDTO dto){
        try{
            userService.forgetPassword(dto.getEmail());
            return new ResponseEntity<>(new StatusResponse("OK","Correo Enviado"),HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(new StatusResponse("BAD",e.getMessage()),HttpStatus.BAD_REQUEST);
        }
    }
}
