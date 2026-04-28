package com.colheplus.controller;

import com.colheplus.model.Lote;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.ArrayList;
import java.util.List;


@RestController
public class LoteController {
    private List<Lote> lotes = new ArrayList<>();

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

    @GetMapping("/lotes")
    public List<Lote> listarLotes(){
        return lotes;
    }

    @PostMapping("/lote")
    public Lote criarLote(@RequestBody Lote lote){
        lotes.add(lote);
        return lote;
    }

}