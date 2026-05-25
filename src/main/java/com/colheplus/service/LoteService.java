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

    private static final String RETIRADA = "RETIRADA";
    private static final String ENTREGA = "ENTREGA";
    private static final String RETIRADA_E_ENTREGA = "RETIRADA_E_ENTREGA";

    private final LoteRepository loteRepository;
    private final AuthService authService;

    public LoteService(LoteRepository loteRepository, AuthService authService) {
        this.loteRepository = loteRepository;
        this.authService = authService;
    }

    public Lote criarLote(Lote lote, String authorizationHeader) {
        Usuario usuario = authService.autenticarComTermo(authorizationHeader, "PRODUTOR");
        validarLote(lote);
        lote.setProdutorId(usuario.getId());
        lote.setProdutorNome(usuario.getNome());
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
        Usuario usuario = authService.autenticarComTermo(authorizationHeader, "PRODUTOR");
        return loteRepository.findAll().stream()
                .filter(lote -> usuario.getId().equals(lote.getProdutorId()))
                .collect(Collectors.toList());
    }

    public List<Lote> listarTodosLotes() {
        return loteRepository.findAll();
    }

    public Lote cancelarLote(Long id, String authorizationHeader) {
        Usuario usuario = authService.autenticarComTermo(authorizationHeader, "PRODUTOR");
        Lote lote = buscarLote(id);
        validarProdutorDoLote(lote, usuario);
        validarLoteAbertoParaAlteracao(lote);
        lote.setStatus("CANCELADO");
        return loteRepository.save(lote);
    }

    public Lote desativarLote(Long id) {
        Lote lote = buscarLote(id);
        lote.setStatus("DESATIVADO");
        return loteRepository.save(lote);
    }

    public void excluirLote(Long id, String authorizationHeader) {
        Usuario usuario = authService.autenticarComTermo(authorizationHeader, "PRODUTOR");
        Lote lote = buscarLote(id);
        validarProdutorDoLote(lote, usuario);
        validarLoteAbertoParaAlteracao(lote);
        loteRepository.delete(lote);
    }

    private Lote buscarLote(Long id) {
        return loteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lote não encontrado"));
    }

    private void validarLote(Lote lote) {
        if (lote.getProduto() == null || lote.getProduto().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Produto é obrigatório");
        }
        lote.setProduto(lote.getProduto().trim());
        if (lote.getProduto().codePoints().noneMatch(Character::isLetter)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Produto deve conter letras");
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
        definirModalidadePadrao(lote);
        if (!RETIRADA.equals(lote.getModalidadeEntrega()) && !ENTREGA.equals(lote.getModalidadeEntrega())
                && !RETIRADA_E_ENTREGA.equals(lote.getModalidadeEntrega())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Modalidade de entrega inválida");
        }
        if (aceitaEntrega(lote) && (lote.getTaxaFixaEntrega() == null || lote.getTaxaFixaEntrega() < 0
                || lote.getRaioMaximoEntregaKm() == null || lote.getRaioMaximoEntregaKm() <= 0)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Entrega exige taxa fixa e raio máximo válidos");
        }
    }

    private void definirModalidadePadrao(Lote lote) {
        if (lote.getModalidadeEntrega() == null || lote.getModalidadeEntrega().trim().isEmpty()) {
            lote.setModalidadeEntrega(lote.getRaioMaximoEntregaKm() == null ? RETIRADA : RETIRADA_E_ENTREGA);
        }
    }

    private boolean aceitaEntrega(Lote lote) {
        return ENTREGA.equals(lote.getModalidadeEntrega()) || RETIRADA_E_ENTREGA.equals(lote.getModalidadeEntrega());
    }

    private void validarProdutorDoLote(Lote lote, Usuario usuario) {
        if (!usuario.getId().equals(lote.getProdutorId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Lote pertence a outro produtor");
        }
    }

    private void validarLoteAbertoParaAlteracao(Lote lote) {
        if (!"ABERTO".equals(lote.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Apenas lotes abertos podem ser alterados");
        }
    }
}
