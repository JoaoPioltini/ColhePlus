package com.colheplus.controller;

import com.colheplus.model.Lote;
import com.colheplus.service.LoteService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class LoteController {

    private final LoteService loteService;

    public LoteController(LoteService loteService) {
        this.loteService = loteService;
    }

    @PostMapping("/lotes")
    public Lote criarLote(
            @RequestBody Lote lote,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        return loteService.criarLote(lote, authorization);
    }

    @GetMapping("/lotes")
    public List<Lote> listarLotes() {
        return loteService.listarLotes();
    }

    @GetMapping("/lotes/meus")
    public List<Lote> listarMeusLotes(
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        return loteService.listarMeusLotes(authorization);
    }

    @DeleteMapping("/lotes/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluirLote(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        loteService.excluirLote(id, authorization);
    }

    @PatchMapping("/lotes/{id}/cancelar")
    public Lote cancelarLote(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        return loteService.cancelarLote(id, authorization);
    }
}
