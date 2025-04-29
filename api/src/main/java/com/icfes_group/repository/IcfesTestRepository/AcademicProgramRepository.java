/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.icfes_group.repository.IcfesTestRepository;

import com.icfes_group.model.IcfesTest.AcademicProgram;
import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author juanc
 */
public interface AcademicProgramRepository extends JpaRepository<AcademicProgram, Long>{
    List<AcademicProgram> findByNombreIn(Set<String> nombres);    
}
