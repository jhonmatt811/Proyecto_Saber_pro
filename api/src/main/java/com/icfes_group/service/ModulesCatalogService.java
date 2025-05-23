package com.icfes_group.service;

import com.icfes_group.model.IcfesTest.ModuleCatalog;
import com.icfes_group.repository.IcfesTestRepository.ModuleCatalogRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ModulesCatalogService {
    private  ModuleCatalogRepository moduleCatalogRepository;

    public List<ModuleCatalog> getAllModules() throws Exception {
        List<ModuleCatalog> modules = moduleCatalogRepository.findAll();
        if(modules.isEmpty()){
            throw  new Exception("No hay modulos registrados");
        }
        return modules;
    }
}
