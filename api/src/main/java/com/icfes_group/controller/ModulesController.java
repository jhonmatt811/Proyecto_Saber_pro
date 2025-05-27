package com.icfes_group.controller;

import com.icfes_group.controller.responses.StatusResponse;
import com.icfes_group.model.IcfesTest.ModuleCatalog;
import com.icfes_group.service.ModulesCatalogService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/modulos")
@AllArgsConstructor
public class ModulesController {
    ModulesCatalogService modulesCatalogService;
    @GetMapping
    public ResponseEntity<?> getModules(){
        try{
            List<ModuleCatalog> catalog = modulesCatalogService.getAllModules();
            return new ResponseEntity<>(catalog, HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(new StatusResponse("BAD",e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
}
