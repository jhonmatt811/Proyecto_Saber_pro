/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.icfes_group.dto;

import lombok.Data;
import java.util.UUID;
import jakarta.validation.constraints.NotBlank;
/**
 *
 * @author juanc
 */
@Data
public class UserDTO{
    private UUID personId;
    @NotBlank(message = "Se necesita una contraseña")
    private String password;
}
