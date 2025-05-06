/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.icfes_group.dto;

import com.icfes_group.model.Rol;
import jakarta.validation.constraints.Email;
import lombok.Data;
import java.util.UUID;
import jakarta.validation.constraints.NotBlank;
/**
 *
 * @author juanc
 */
@Data
public class UserDTO {
        private PersonaDTO person;
    private UUID personId;
    @Email
    private String email;
    private String password;
    private Integer rol_id;
    private Rol rol;

}
