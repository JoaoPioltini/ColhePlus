package com.colheplus.service;

import com.colheplus.model.Lote;
import com.colheplus.model.Usuario;
import com.colheplus.repository.LoteRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class LoteService {

    private final LoteRepository loteRepository;
    private final AuthService authService;

    public LoteService(LoteRepository loteRepository, AuthService authService) {
        this.loteRepository = loteRepository;
        this.authService = authService;
    }

    public Lote criarLote(Lote lote, String authorizationHeader) {
        validarLote(lote);
        if (authorizationHeader != null) {
            Usuario usuario = authService.buscarPorAuthorizationHeader(authorizationHeader);
            lote.setProdutorId(usuario.getId());
            lote.setProdutorNome(usuario.getNome());
        }
        if (lote.getStatus() == null) {
            lote.setStatus("ABERTO");
        }
        if (lote.getVolumeAgrupado() == null) {
            lote.setVolumeAgrupado(0.0);
        }
        return loteRepository.save(lote);
    }

    public List<Lote> listarLotes() {
        return loteRepository.findAll().stream()
                .filter(lote -> "ABERTO".equals(lote.getStatus()))
                .collect(Collectors.toList());
    }

    public List<Lote> listarMeusLotes(String authorizationHeader) {
        Usuario usuario = authService.buscarPorAuthorizationHeader(authorizationHeader);
        return loteRepository.findAll().stream()
                .filter(lote -> usuario.getId().equals(lote.getProdutorId()) || lote.getProdutorId() == null)
                .collect(Collectors.toList());
    }

    public Lote cancelarLote(Long id) {
        Lote lote = buscarLote(id);
        lote.setStatus("CANCELADO");
        return loteRepository.save(lote);
    }

    public Lote desativarLote(Long id) {
        Lote lote = buscarLote(id);
        lote.setStatus("DESATIVADO");
        return loteRepository.save(lote);
    }

    public void excluirLote(Long id) {
        if (!loteRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Lote não encontrado");
        }
        loteRepository.deleteById(id);
    }

    private Lote buscarLote(Long id) {
        return loteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lote não encontrado"));
    }

    private void validarLote(Lote lote) {
        if (lote.getProduto() == null || lote.getProduto().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Produto é obrigatório");
        }
        if (lote.getVolumeDisponivelKg() == null || lote.getVolumeDisponivelKg() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Volume disponível deve ser maior que zero");
        }
        if (lote.getVolumeMinimoViavelKg() == null || lote.getVolumeMinimoViavelKg() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Volume mínimo viável deve ser maior que zero");
        }
        if (lote.getPrecoPorKg() == null || lote.getPrecoPorKg() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Preço por kg deve ser maior que zero");
        }
    }
}
