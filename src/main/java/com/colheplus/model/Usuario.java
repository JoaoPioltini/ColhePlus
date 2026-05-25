package com.colheplus.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    @Column(unique = true, nullable = false)
    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String senhaHash;

    private String papel;
    private Boolean termoAceito = false;
    private LocalDateTime dataHoraAceiteTermo;
    private String versaoTermoAceita;
    private Boolean ativo = true;
    private Double latitude;
    private Double longitude;

    @JsonIgnore
    private String tokenSessao;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenhaHash() {
        return senhaHash;
    }

    public void setSenhaHash(String senhaHash) {
        this.senhaHash = senhaHash;
    }

    public String getPapel() {
        return papel;
    }

    public void setPapel(String papel) {
        this.papel = papel;
    }

    public Boolean getTermoAceito() {
        return termoAceito;
    }

    public void setTermoAceito(Boolean termoAceito) {
        this.termoAceito = termoAceito;
    }

    public LocalDateTime getDataHoraAceiteTermo() {
        return dataHoraAceiteTermo;
    }

    public void setDataHoraAceiteTermo(LocalDateTime dataHoraAceiteTermo) {
        this.dataHoraAceiteTermo = dataHoraAceiteTermo;
    }

    public String getVersaoTermoAceita() {
        return versaoTermoAceita;
    }

    public void setVersaoTermoAceita(String versaoTermoAceita) {
        this.versaoTermoAceita = versaoTermoAceita;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getTokenSessao() {
        return tokenSessao;
    }

    public void setTokenSessao(String tokenSessao) {
        this.tokenSessao = tokenSessao;
    }
}
