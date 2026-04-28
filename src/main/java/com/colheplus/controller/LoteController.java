package com.colheplus.controller;

import com.colheplus.model.Lote;
import com.colheplus.service.LoteService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class LoteController {

    private final LoteService loteService;

    public LoteController(LoteService loteService) {
        this.loteService = loteService;
    }

    @PostMapping("/lote")
    public Lote criarLote(@RequestBody Lote lote) {
        return loteService.criarLote(lote);
    }

    @GetMapping("/lotes")
    public List<Lote> listarLotes() {
        return loteService.listarLotes();
    }
}