package com.icfes_group.controller;

import com.icfes_group.controller.responses.StatusResponse;
import com.icfes_group.service.RolService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.icfes_group.model.Rol;

import java.util.List;

@RestController
@RequestMapping("/roles")
@AllArgsConstructor
public class RolesController {
    private final RolService rolService;
    @GetMapping
    public ResponseEntity<?> getRoles() {
        try{
            List<Rol> response = rolService.findAll();
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new StatusResponse("BAD",e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
}
