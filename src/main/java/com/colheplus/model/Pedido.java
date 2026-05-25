package com.colheplus.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Entity
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Positive
    private Double quantidadeKg;

    @NotNull
    private Long loteId;
    private Long compradorId;
    private String compradorNome;
    private String loteProduto;
    private String tipoEntrega;
    private Double latitudeEntrega;
    private Double longitudeEntrega;
    private Double distanciaKm;
    private Double latitudeRetirada;
    private Double longitudeRetirada;
    private String horarioRetirada;
    private String instrucoesRetirada;
    private String codigoRetirada;
    private String status = "PENDENTE";

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getQuantidadeKg() {
        return quantidadeKg;
    }

    public void setQuantidadeKg(Double quantidadeKg) {
        this.quantidadeKg = quantidadeKg;
    }

    public Double getQuantidade() {
        return quantidadeKg;
    }

    public void setQuantidade(Double quantidade) {
        this.quantidadeKg = quantidade;
    }

    public Long getLoteId() {
        return loteId;
    }

    public void setLoteId(Long loteId) {
        this.loteId = loteId;
    }

    public Long getCompradorId() {
        return compradorId;
    }

    public void setCompradorId(Long compradorId) {
        this.compradorId = compradorId;
    }

    public String getCompradorNome() {
        return compradorNome;
    }

    public void setCompradorNome(String compradorNome) {
        this.compradorNome = compradorNome;
    }

    public String getLoteProduto() {
        return loteProduto;
    }

    public void setLoteProduto(String loteProduto) {
        this.loteProduto = loteProduto;
    }

    public String getTipoEntrega() {
        return tipoEntrega;
    }

    public void setTipoEntrega(String tipoEntrega) {
        this.tipoEntrega = tipoEntrega;
    }

    public Double getLatitudeEntrega() {
        return latitudeEntrega;
    }

    public void setLatitudeEntrega(Double latitudeEntrega) {
        this.latitudeEntrega = latitudeEntrega;
    }

    public Double getLongitudeEntrega() {
        return longitudeEntrega;
    }

    public void setLongitudeEntrega(Double longitudeEntrega) {
        this.longitudeEntrega = longitudeEntrega;
    }

    public Double getDistanciaKm() {
        return distanciaKm;
    }

    public void setDistanciaKm(Double distanciaKm) {
        this.distanciaKm = distanciaKm;
    }

    public Double getLatitudeRetirada() {
        return latitudeRetirada;
    }

    public void setLatitudeRetirada(Double latitudeRetirada) {
        this.latitudeRetirada = latitudeRetirada;
    }

    public Double getLongitudeRetirada() {
        return longitudeRetirada;
    }

    public void setLongitudeRetirada(Double longitudeRetirada) {
        this.longitudeRetirada = longitudeRetirada;
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

    public String getCodigoRetirada() {
        return codigoRetirada;
    }

    public void setCodigoRetirada(String codigoRetirada) {
        this.codigoRetirada = codigoRetirada;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
