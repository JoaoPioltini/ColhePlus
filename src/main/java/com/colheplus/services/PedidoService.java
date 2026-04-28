package com.colheplus.service;

import com.colheplus.model.Lote;
import com.colheplus.model.Pedido;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PedidoService {

    private List<Pedido> pedidos = new ArrayList<>();
    private final LoteService loteService;

    public PedidoService(LoteService loteService) {
        this.loteService = loteService;
    }

    public Pedido criarPedido(Pedido pedido) {

        pedidos.add(pedido);

        for (Lote lote : loteService.getLotes()) {

            if (lote.getId().equals(pedido.getLoteId())) {

                double soma = 0;

                for (Pedido p : pedidos) {
                    if (p.getLoteId().equals(lote.getId())) {
                        soma += p.getQuantidade();
                    }
                }

                if (soma >= lote.getVolumeMinimo()) {
                    lote.setStatus("ATIVADO");
                }
            }
        }

        return pedido;
    }

    public List<Pedido> listarPedidos() {
        return pedidos;
    }
}