package com.colheplus.service;

import com.colheplus.model.Lote;
import com.colheplus.model.Usuario;
import com.colheplus.repository.UsuarioRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AdminService {

    private final UsuarioRepository usuarioRepository;
    private final LoteService loteService;
    private final AuthService authService;

    public AdminService(UsuarioRepository usuarioRepository, LoteService loteService, AuthService authService) {
        this.usuarioRepository = usuarioRepository;
        this.loteService = loteService;
        this.authService = authService;
    }

    public List<Usuario> listarUsuarios(String authorizationHeader) {
        authService.autenticarComTermo(authorizationHeader, "ADMIN");
        return usuarioRepository.findAll();
    }

    public List<Lote> listarLotes(String authorizationHeader) {
        authService.autenticarComTermo(authorizationHeader, "ADMIN");
        return loteService.listarTodosLotes();
    }

    public void excluirUsuario(Long id, String authorizationHeader) {
        authService.autenticarComTermo(authorizationHeader, "ADMIN");
        if (!usuarioRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado");
        }
        usuarioRepository.deleteById(id);
    }

    public Lote desativarLote(Long id, String authorizationHeader) {
        authService.autenticarComTermo(authorizationHeader, "ADMIN");
        return loteService.desativarLote(id);
    }
}
