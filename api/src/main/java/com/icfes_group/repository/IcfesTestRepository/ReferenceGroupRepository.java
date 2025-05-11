/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.icfes_group.repository.IcfesTestRepository;

import com.icfes_group.model.IcfesTest.ReferenceGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Set;
/**
 *
 * @author juanc
 */
public interface ReferenceGroupRepository extends JpaRepository<ReferenceGroup, Long>{
}
