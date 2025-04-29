/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.icfes_group.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Data;
import com.icfes_group.dto.PersonaDTO;
/**
 *
 * @author juanc
 */
@Entity
@Data
@Table(name = "personas")
public class Persona {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(unique = true)
    private Long cc;
    private String primer_nombre;
    private String segundo_nombre;
    private String primer_apellido;
    private String segundo_apellido;    
    private String email;        
    
    public Persona(PersonaDTO dto){
        this.cc = dto.getCc();
        this.primer_nombre = dto.getPrimer_nombre();
        this.segundo_nombre = dto.getSegundo_nombre();
        this.primer_apellido = dto.getPrimer_apellido();
        this.segundo_apellido = dto.getSegundo_apellido();
        this.email = dto.getEmail();
    }
    public Persona(){}
}
