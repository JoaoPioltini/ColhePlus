package com.colheplus.service;

import com.colheplus.model.Lote;
import com.colheplus.model.Pedido;
import com.colheplus.repository.LoteRepository;
import com.colheplus.repository.PedidoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final LoteRepository loteRepository;

    public PedidoService(PedidoRepository pedidoRepository, LoteRepository loteRepository) {
        this.pedidoRepository = pedidoRepository;
        this.loteRepository = loteRepository;
    }

    public Pedido criarPedido(Pedido pedido) {

        // validação básica (evita null pointer que você teve)
        if (pedido.getLoteId() == null) {
            throw new RuntimeException("LoteId não pode ser null");
        }

        // 1. buscar o lote
        Lote lote = loteRepository.findById(pedido.getLoteId())
                .orElseThrow(() -> new RuntimeException("Lote não encontrado"));

        // 2. buscar pedidos do lote
        List<Pedido> pedidosDoLote = pedidoRepository.findByLoteId(lote.getId());

        // 3. calcular soma atual
        double soma = 0;
        for (Pedido p : pedidosDoLote) {
            soma += p.getQuantidade();
        }

        // 4. soma futura
        double somaFutura = soma + pedido.getQuantidade();

        // 5. bloquear excesso
        if (somaFutura > lote.getVolumeDisponivel()) {
            throw new RuntimeException("Pedido ultrapassa o volume disponível do lote");
        }

        // 6. salvar pedido
        Pedido pedidoSalvo = pedidoRepository.save(pedido);

        // 7. ativar lote
        if (somaFutura >= lote.getVolumeMinimo()) {
            lote.setStatus("ATIVADO");
            loteRepository.save(lote);
        }

        return pedidoSalvo;
    }

    public List<Pedido> listarPedidos() {
        return pedidoRepository.findAll();
    }
}