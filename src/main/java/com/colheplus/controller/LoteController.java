package com.colheplus.controller;

import com.colheplus.model.Lote;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoteController {

    @GetMapping("/lote")
    public Lote getLote() {

        Lote lote = new Lote();
        lote.setId(1L);
        lote.setProduto("Tomate");
        lote.setVolumeDisponivel(1000.0);
        lote.setVolumeMinimo(500.0);
        lote.setPrecoPorKg(5.0);
        lote.setStatus("ABERTO");

        return lote;
    }
}