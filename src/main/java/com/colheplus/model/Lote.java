package com.colheplus.model;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Lote {

    @Id
    private Long id;
    
    private String produto;
    private Double volumeDisponivel;
    private Double volumeMinimo;
    private Double precoPorKg;
    private String status;

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

    public Double getVolumeDisponivel() {
       return volumeDisponivel;
    }   

    public void setVolumeDisponivel(Double volumeDisponivel) {
        this.volumeDisponivel = volumeDisponivel;
    }

    public Double getVolumeMinimo() {
        return volumeMinimo;
    }

    public void setVolumeMinimo(Double volumeMinimo) {
        this.volumeMinimo = volumeMinimo;
    }

    public Double getPrecoPorKg() {
    return precoPorKg;
    }

    public void setPrecoPorKg(Double precoPorKg) {
        this.precoPorKg = precoPorKg;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
} 