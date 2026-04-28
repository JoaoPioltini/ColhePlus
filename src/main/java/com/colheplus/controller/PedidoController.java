package com.colheplus.controller;

import com.colheplus.model.Pedido;
import com.colheplus.service.PedidoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @PostMapping("/pedido")
    public Pedido criarPedido(@RequestBody Pedido pedido) {
        return pedidoService.criarPedido(pedido);
    }

    @GetMapping("/pedidos")
    public List<Pedido> listarPedidos() {
        return pedidoService.listarPedidos();
    }
}