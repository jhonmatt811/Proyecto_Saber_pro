/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.icfes_group.repository.IcfesTestRepository;

import com.icfes_group.model.IcfesTest.Evaluated;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author juanc
 */
public interface EvaluatedRepository extends JpaRepository<Evaluated, UUID>{
    
}
