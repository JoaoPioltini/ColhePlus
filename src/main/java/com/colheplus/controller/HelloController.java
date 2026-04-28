package com.colheplus.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController // tranforma a classe em API
public class HelloController {
    @GetMapping("/hello") // criar a rota
    public String hello(){
        return "Backend Colhe Plus Funcionandooo";
    }

}