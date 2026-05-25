package com.colheplus.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Lote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String produto;
    private Double volumeDisponivelKg;
    private Double volumeMinimoViavelKg;
    private Double precoPorKg;
    private String modalidadeEntrega;
    private Double taxaFixaEntrega;
    private Double raioMaximoEntregaKm;
    private String horarioRetirada;
    private String instrucoesRetirada;
    private Double volumeAgrupado = 0.0;
    private Long produtorId;
    private String produtorNome;
    private String status = "ABERTO";

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProduto() {
        return produto;
    }

    public void setProduto(String produto) {
        this.produto = produto;
    }

    public Double getVolumeDisponivelKg() {
        return volumeDisponivelKg;
    }

    public void setVolumeDisponivelKg(Double volumeDisponivelKg) {
        this.volumeDisponivelKg = volumeDisponivelKg;
    }

    public Double getVolumeMinimoViavelKg() {
        return volumeMinimoViavelKg;
    }

    public void setVolumeMinimoViavelKg(Double volumeMinimoViavelKg) {
        this.volumeMinimoViavelKg = volumeMinimoViavelKg;
    }

    public Double getPrecoPorKg() {
        return precoPorKg;
    }

    public void setPrecoPorKg(Double precoPorKg) {
        this.precoPorKg = precoPorKg;
    }

    public Double getTaxaFixaEntrega() {
        return taxaFixaEntrega;
    }

    public String getModalidadeEntrega() {
        return modalidadeEntrega;
    }

    public void setModalidadeEntrega(String modalidadeEntrega) {
        this.modalidadeEntrega = modalidadeEntrega;
    }

    public void setTaxaFixaEntrega(Double taxaFixaEntrega) {
        this.taxaFixaEntrega = taxaFixaEntrega;
    }

    public Double getRaioMaximoEntregaKm() {
        return raioMaximoEntregaKm;
    }

    public void setRaioMaximoEntregaKm(Double raioMaximoEntregaKm) {
        this.raioMaximoEntregaKm = raioMaximoEntregaKm;
    }

    public String getHorarioRetirada() {
        return horarioRetirada;
    }

    public void setHorarioRetirada(String horarioRetirada) {
        this.horarioRetirada = horarioRetirada;
    }

    public String getInstrucoesRetirada() {
        return instrucoesRetirada;
    }

    public void setInstrucoesRetirada(String instrucoesRetirada) {
        this.instrucoesRetirada = instrucoesRetirada;
    }

    public Double getVolumeAgrupado() {
        return volumeAgrupado;
    }

    public void setVolumeAgrupado(Double volumeAgrupado) {
        this.volumeAgrupado = volumeAgrupado;
    }

    public Long getProdutorId() {
        return produtorId;
    }

    public void setProdutorId(Long produtorId) {
        this.produtorId = produtorId;
    }

    public String getProdutorNome() {
        return produtorNome;
    }

    public void setProdutorNome(String produtorNome) {
        this.produtorNome = produtorNome;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
} 
