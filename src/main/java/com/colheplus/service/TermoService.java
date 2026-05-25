package com.colheplus.service;

import com.colheplus.model.TermoResponsabilidade;
import com.colheplus.repository.TermoResponsabilidadeRepository;
import java.time.LocalDate;
import org.springframework.stereotype.Service;

@Service
public class TermoService {

    private final TermoResponsabilidadeRepository termoRepository;

    public TermoService(TermoResponsabilidadeRepository termoRepository) {
        this.termoRepository = termoRepository;
    }

    public synchronized TermoResponsabilidade buscarTermoAtual() {
        return termoRepository.findFirstByAtivoTrueOrderByDataPublicacaoDescIdDesc()
                .orElseGet(this::criarTermoInicial);
    }

    private TermoResponsabilidade criarTermoInicial() {
        TermoResponsabilidade termo = new TermoResponsabilidade();
        termo.setVersao("1.0");
        termo.setConteudo("Ao prosseguir, você concorda com o uso responsável da plataforma Colhe+.");
        termo.setDataPublicacao(LocalDate.of(2026, 5, 12));
        termo.setAtivo(true);
        return termoRepository.save(termo);
    }
}
