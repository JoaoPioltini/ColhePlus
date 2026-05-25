package com.colheplus.service;

import org.springframework.stereotype.Service;

@Service
public class GeolocalizacaoService {

    private static final double RAIO_TERRA_KM = 6371.0;

    public double calcularDistanciaKm(double latitudeOrigem, double longitudeOrigem,
            double latitudeDestino, double longitudeDestino) {
        double deltaLatitude = Math.toRadians(latitudeDestino - latitudeOrigem);
        double deltaLongitude = Math.toRadians(longitudeDestino - longitudeOrigem);
        double latitudeOrigemRad = Math.toRadians(latitudeOrigem);
        double latitudeDestinoRad = Math.toRadians(latitudeDestino);

        double haversine = Math.pow(Math.sin(deltaLatitude / 2), 2)
                + Math.cos(latitudeOrigemRad) * Math.cos(latitudeDestinoRad)
                * Math.pow(Math.sin(deltaLongitude / 2), 2);
        return RAIO_TERRA_KM * 2 * Math.atan2(Math.sqrt(haversine), Math.sqrt(1 - haversine));
    }
}
