/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.icfes_group.dto;
import lombok.Data;
import java.util.UUID;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
/**
 *
 * @author juanc
 */

@Data
public class PersonaDTO {
    private UUID id;
    
    @NotNull(message = "El número de cédula es obligatorio")
    private Long cc;
    
    @NotBlank(message = "El primer nombre es obligatorio")
    private String primer_nombre;
    
    @NotBlank(message = "El segundo nombre es obligatorio")
    private String segundo_nombre;
    
    @NotBlank(message = "El primer apellido es obligatorio")
    private String primer_apellido;
    
    @NotBlank(message = "El segundo apellido es obligatorio")
    private String segundo_apellido;
    
    @NotBlank(message = "El email es obligatorio")
    private String email;
}
