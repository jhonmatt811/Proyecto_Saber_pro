/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.icfes_group.service;

import com.icfes_group.model.IcfesTest.AcademicProgram;
import com.icfes_group.repository.IcfesTestRepository.AcademicProgramRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author juanc
 */
@Service
public class AcademicProgramsService {
    @Autowired
    AcademicProgramRepository academicProgramRepository;
    public List<AcademicProgram> getPrograms() throws Exception{
        List<AcademicProgram> listPrograms = academicProgramRepository.findAll();
        if(listPrograms.size() <= 0){
            throw new Exception("No hay programas registrados");
        }
        return listPrograms;
    }
}
