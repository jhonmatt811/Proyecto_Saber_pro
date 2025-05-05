/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.icfes_group.repository.IcfesTestRepository;

import com.icfes_group.model.IcfesTest.ModuleResult;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author juanc
 */
public interface ModuleResultRepository extends JpaRepository<ModuleResult, UUID>{
    @Query("SELECT rm FROM ModuleResult rm " +
       "WHERE rm.moduleCatalog.id = :catalogoModuloId " +
       "AND rm.globalResult.id = :resultadoGlobalId")
    Optional<ModuleResult> findByGlobalRsltAndCatId(
            @Param("catalogoModuloId") Long catalogoModuloId,
            @Param("resultadoGlobalId") String resultadoGlobalId
    );

}
