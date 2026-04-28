package com.colheplus.service;

import com.colheplus.model.Lote;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LoteService {

    private List<Lote> lotes = new ArrayList<>();

    public Lote criarLote(Lote lote) {
        lotes.add(lote);
        return lote;
    }

    public List<Lote> listarLotes() {
        return lotes;
    }

    public List<Lote> getLotes() {
        return lotes;
    }
}