package com.colheplus.controller;

import com.colheplus.model.Pedido;
import com.colheplus.service.PedidoService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@RestController
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @PostMapping("/pedidos")
    public Pedido criarPedido(
            @RequestBody @Valid Pedido pedido,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        return pedidoService.criarPedido(pedido, authorization);
    }

    @GetMapping("/pedidos")
    public List<Pedido> listarPedidos(
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        return pedidoService.listarPedidos(authorization);
    }

    @PatchMapping("/pedidos/{id}/cancelar")
    public Pedido cancelarPedido(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        return pedidoService.cancelarPedido(id, authorization);
    }
}
