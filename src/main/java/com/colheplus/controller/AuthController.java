package com.colheplus.controller;

import com.colheplus.dto.AuthResponse;
import com.colheplus.dto.LoginRequest;
import com.colheplus.dto.TermoResponse;
import com.colheplus.model.Usuario;
import com.colheplus.model.TermoResponsabilidade;
import com.colheplus.service.AuthService;
import com.colheplus.service.TermoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    private final AuthService authService;
    private final TermoService termoService;

    public AuthController(AuthService authService, TermoService termoService) {
        this.authService = authService;
        this.termoService = termoService;
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
        TermoResponsabilidade termo = termoService.buscarTermoAtual();
        return new TermoResponse(termo.getId(), termo.getVersao(), termo.getConteudo(), termo.getDataPublicacao());
    }

    @PostMapping("/auth/aceite")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void aceitarTermo(@RequestHeader(value = "Authorization", required = false) String authorization) {
        authService.aceitarTermo(authorization);
    }

    @GetMapping("/usuarios/me")
    public Usuario buscarPerfil(@RequestHeader(value = "Authorization", required = false) String authorization) {
        return authService.buscarPerfil(authorization);
    }

    @PutMapping("/usuarios/me")
    public Usuario atualizarPerfil(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestBody Usuario usuario) {
        return authService.atualizarPerfil(authorization, usuario);
    }
}
