package com.colheplus.dto;

import java.time.LocalDate;

public class TermoResponse {

    private Long id;
    private String versao;
    private String conteudo;
    private LocalDate dataPublicacao;

    public TermoResponse(Long id, String versao, String conteudo, LocalDate dataPublicacao) {
        this.id = id;
        this.versao = versao;
        this.conteudo = conteudo;
        this.dataPublicacao = dataPublicacao;
    }

    public Long getId() {
        return id;
    }

    public String getVersao() {
        return versao;
    }

    public String getConteudo() {
        return conteudo;
    }

    public LocalDate getDataPublicacao() {
        return dataPublicacao;
    }
}
