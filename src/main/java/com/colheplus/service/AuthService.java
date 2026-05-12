package com.colheplus.service;

import com.colheplus.dto.AuthResponse;
import com.colheplus.dto.LoginRequest;
import com.colheplus.model.Usuario;
import com.colheplus.repository.UsuarioRepository;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;

    public AuthService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario cadastrar(Usuario usuario) {
        validarCadastro(usuario);
        usuario.setEmail(usuario.getEmail().trim().toLowerCase());
        usuario.setTermoAceito(false);
        usuario.setAtivo(true);
        return usuarioRepository.save(usuario);
    }

    public AuthResponse login(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(normalizarEmail(request.getEmail()))
                .orElseThrow(() -> erroCredenciais());

        if (!usuario.getSenhaHash().equals(request.getSenha())) {
            throw erroCredenciais();
        }

        usuario.setTokenSessao(UUID.randomUUID().toString());
        Usuario salvo = usuarioRepository.save(usuario);
        return new AuthResponse(salvo.getTokenSessao(), salvo);
    }

    public void aceitarTermo(String authorizationHeader) {
        Usuario usuario = buscarPorAuthorizationHeader(authorizationHeader);
        usuario.setTermoAceito(true);
        usuarioRepository.save(usuario);
    }

    public Usuario buscarPorAuthorizationHeader(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token ausente");
        }
        String token = authorizationHeader.substring("Bearer ".length());
        return usuarioRepository.findByTokenSessao(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token inválido"));
    }

    private void validarCadastro(Usuario usuario) {
        if (campoVazio(usuario.getNome()) || campoVazio(usuario.getEmail()) || campoVazio(usuario.getSenhaHash())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nome, e-mail e senha são obrigatórios");
        }
        if (usuarioRepository.existsByEmail(normalizarEmail(usuario.getEmail()))) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "E-mail já cadastrado");
        }
        if ("PRODUTOR".equals(usuario.getPapel())
                && (usuario.getLatitude() == null || usuario.getLongitude() == null)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Produtor precisa informar localização");
        }
    }

    private boolean campoVazio(String valor) {
        return valor == null || valor.trim().isEmpty();
    }

    private String normalizarEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase();
    }

    private ResponseStatusException erroCredenciais() {
        return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciais inválidas");
    }
}
