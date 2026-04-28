package com.colheplus.controller;

import com.colheplus.model.Pedido;
import com.colheplus.service.PedidoService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@RestController
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @PostMapping("/pedido")
    public Pedido criarPedido(@RequestBody @Valid Pedido pedido) {
        return pedidoService.criarPedido(pedido);
    }

    @GetMapping("/pedidos")
    public List<Pedido> listarPedidos() {
        return pedidoService.listarPedidos();
    }
}