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

    public AdminService(UsuarioRepository usuarioRepository, LoteService loteService) {
        this.usuarioRepository = usuarioRepository;
        this.loteService = loteService;
    }

    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    public void excluirUsuario(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado");
        }
        usuarioRepository.deleteById(id);
    }

    public Lote desativarLote(Long id) {
        return loteService.desativarLote(id);
    }
}
