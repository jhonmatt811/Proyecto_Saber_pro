/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.icfes_group.repository;

import com.icfes_group.model.Persona;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

/**
 *
 * @author juanc
 */
public interface PersonaRepository extends JpaRepository<Persona, UUID>{
    
}
