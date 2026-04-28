package com.colheplus.service;

import com.colheplus.model.Lote;
import com.colheplus.repository.LoteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LoteService {

    private final LoteRepository loteRepository;

    public LoteService(LoteRepository loteRepository) {
        this.loteRepository = loteRepository;
    }

    public Lote criarLote(Lote lote) {
        return loteRepository.save(lote);
    }

    public List<Lote> listarLotes() {
        return loteRepository.findAll();
    }

    public List<Lote> getLotes() {
        return loteRepository.findAll();
    }
}