package com.icfes_group.controller;

import com.icfes_group.controller.responses.StatusResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {
    @GetMapping(path = "/", produces = "application/json")
    public StatusResponse home() {
        return new StatusResponse("ok","conecto");
    }
}
