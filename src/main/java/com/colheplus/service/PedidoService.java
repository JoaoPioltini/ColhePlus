package com.colheplus.service;

import com.colheplus.model.Lote;
import com.colheplus.model.Pedido;
import com.colheplus.model.Usuario;
import com.colheplus.repository.LoteRepository;
import com.colheplus.repository.PedidoRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final LoteRepository loteRepository;
    private final AuthService authService;

    public PedidoService(PedidoRepository pedidoRepository, LoteRepository loteRepository, AuthService authService) {
        this.pedidoRepository = pedidoRepository;
        this.loteRepository = loteRepository;
        this.authService = authService;
    }

    public Pedido criarPedido(Pedido pedido, String authorizationHeader) {
        if (pedido.getLoteId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "LoteId não pode ser null");
        }
        if (pedido.getQuantidadeKg() == null || pedido.getQuantidadeKg() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quantidade deve ser maior que zero");
        }

        Lote lote = loteRepository.findById(pedido.getLoteId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lote não encontrado"));

        if (!"ABERTO".equals(lote.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Este lote não está aberto para pedidos");
        }

        double soma = calcularVolumeAgrupado(lote.getId());
        double somaFutura = soma + pedido.getQuantidadeKg();

        if (somaFutura > lote.getVolumeDisponivelKg()) {
            double disponivel = lote.getVolumeDisponivelKg() - soma;
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Pedido ultrapassa o volume disponível do lote. Disponível para compra: " + disponivel + " kg");
        }

        preencherDadosPedido(pedido, lote, authorizationHeader);
        Pedido pedidoSalvo = pedidoRepository.save(pedido);
        lote.setVolumeAgrupado(somaFutura);

        if (somaFutura >= lote.getVolumeMinimoViavelKg()) {
            lote.setStatus("ATIVADO");
        }

        loteRepository.save(lote);
        return pedidoSalvo;
    }

    public List<Pedido> listarPedidos(String authorizationHeader) {
        Usuario usuario = authService.buscarPorAuthorizationHeader(authorizationHeader);
        List<Pedido> pedidos = pedidoRepository.findAll();
        if ("PRODUTOR".equals(usuario.getPapel())) {
            return pedidos.stream()
                    .filter(pedido -> pedidoPertenceAoProdutor(pedido, usuario.getId()))
                    .toList();
        }
        if ("COMPRADOR".equals(usuario.getPapel())) {
            return pedidos.stream()
                    .filter(pedido -> usuario.getId().equals(pedido.getCompradorId()) || pedido.getCompradorId() == null)
                    .toList();
        }
        return pedidos;
    }

    public Pedido cancelarPedido(Long id, String authorizationHeader) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido não encontrado"));
        Usuario usuario = authService.buscarPorAuthorizationHeader(authorizationHeader);
        if ("COMPRADOR".equals(usuario.getPapel()) && pedido.getCompradorId() != null
                && !usuario.getId().equals(pedido.getCompradorId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Pedido pertence a outro comprador");
        }
        if (!"PENDENTE".equals(pedido.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Apenas pedidos pendentes podem ser cancelados");
        }
        pedido.setStatus("CANCELADO");
        Pedido salvo = pedidoRepository.save(pedido);
        recalcularLote(pedido.getLoteId());
        return salvo;
    }

    private double calcularVolumeAgrupado(Long loteId) {
        double soma = 0;
        for (Pedido pedido : pedidoRepository.findByLoteId(loteId)) {
            if (!"CANCELADO".equals(pedido.getStatus())) {
                soma += pedido.getQuantidadeKg();
            }
        }
        return soma;
    }

    private void preencherDadosPedido(Pedido pedido, Lote lote, String authorizationHeader) {
        pedido.setStatus("PENDENTE");
        pedido.setLoteProduto(lote.getProduto());
        if (authorizationHeader != null) {
            Usuario usuario = authService.buscarPorAuthorizationHeader(authorizationHeader);
            pedido.setCompradorId(usuario.getId());
            pedido.setCompradorNome(usuario.getNome());
        }
    }

    private boolean pedidoPertenceAoProdutor(Pedido pedido, Long produtorId) {
        return loteRepository.findById(pedido.getLoteId())
                .map(lote -> produtorId.equals(lote.getProdutorId()) || lote.getProdutorId() == null)
                .orElse(false);
    }

    private void recalcularLote(Long loteId) {
        Lote lote = loteRepository.findById(loteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lote não encontrado"));
        double soma = calcularVolumeAgrupado(loteId);
        lote.setVolumeAgrupado(soma);
        if ("ATIVADO".equals(lote.getStatus()) && soma < lote.getVolumeMinimoViavelKg()) {
            lote.setStatus("ABERTO");
        }
        loteRepository.save(lote);
    }
}
