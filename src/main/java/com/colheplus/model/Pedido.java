package com.colheplus.model;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;


@Entity
public class Pedido {
    
    @Id
    private Long id;

    private Double quantidade;
    private Long loteId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Double quantidade) {
        this.quantidade = quantidade;
    }

    public Long getLoteId() {
        return loteId;
    }

    public void setLoteId(Long loteId) {
        this.loteId = loteId;
    }
}