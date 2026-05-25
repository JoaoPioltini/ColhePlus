package com.colheplus.repository;

import com.colheplus.model.TermoResponsabilidade;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TermoResponsabilidadeRepository extends JpaRepository<TermoResponsabilidade, Long> {

    Optional<TermoResponsabilidade> findFirstByAtivoTrueOrderByDataPublicacaoDescIdDesc();
}
