package com.icfes_group.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {
    @GetMapping(path = "/", produces = "application/json")
    public StatusResponse home() {
        return new StatusResponse("OK", "Conectado correctamente");
    }
}
