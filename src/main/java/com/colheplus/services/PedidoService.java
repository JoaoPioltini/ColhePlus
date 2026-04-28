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

        // salva o pedido no banco
        pedidoRepository.save(pedido);

        // busca o lote correspondente
        Lote lote = loteRepository.findById(pedido.getLoteId()).orElse(null);

        if (lote != null) {

            double soma = 0;

            for (Pedido p : pedidoRepository.findAll()) {
                if (p.getLoteId().equals(lote.getId())) {
                    soma += p.getQuantidade();
                }
            }

            if (soma >= lote.getVolumeMinimo()) {
                lote.setStatus("ATIVADO");
                loteRepository.save(lote);
            }
        }
        return pedido;
    }

    public List<Pedido> listarPedidos() {
        return pedidoRepository.findAll();
    }
}