package com.colheplus.controller;

import com.colheplus.dto.AuthResponse;
import com.colheplus.dto.LoginRequest;
import com.colheplus.dto.TermoResponse;
import com.colheplus.model.Usuario;
import com.colheplus.service.AuthService;
import java.time.LocalDate;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/auth/cadastro")
    @ResponseStatus(HttpStatus.CREATED)
    public Usuario cadastrar(@RequestBody Usuario usuario) {
        return authService.cadastrar(usuario);
    }

    @PostMapping("/auth/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @GetMapping("/termo")
    public TermoResponse getTermo() {
        return new TermoResponse(
                1L,
                "1.0",
                "Ao prosseguir, você concorda com o uso responsável da plataforma Colhe+.",
                LocalDate.of(2026, 5, 12));
    }

    @PostMapping("/auth/aceite")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void aceitarTermo(@RequestHeader(value = "Authorization", required = false) String authorization) {
        authService.aceitarTermo(authorization);
    }
}
