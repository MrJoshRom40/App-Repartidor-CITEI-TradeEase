package com.example.repartidor;

public class CheckKilometraje {

    private static final double EARTH_RADIUS_KM = 6371.01;
    private double latitudOrigen;
    private double longitudOrigen;

    private double latitudDestino;
    private double longitudDestino;

    public CheckKilometraje(double latitudOrigen, double longitudOrigen, double latitudDestino, double longitudDestino){
        this.latitudOrigen = latitudOrigen;
        this.longitudOrigen = longitudOrigen;
        this.latitudDestino = latitudDestino;
        this.longitudDestino = longitudDestino;
    }

    public double getKilometrosRecorridos(){
        double latitudOrigenRAD = Math.toRadians(latitudOrigen);
        double longitudOrigenRAD = Math.toRadians(longitudOrigen);
        double latitudDestinoRAD = Math.toRadians(latitudDestino);
        double longitudDestinoRAD = Math.toRadians(longitudDestino);

        double deltaLat = latitudDestinoRAD - latitudOrigenRAD;
        double deltaLon = longitudDestinoRAD - longitudOrigenRAD;

        double a = Math.sin(deltaLat/2) * Math.sin(deltaLat/2) + Math.cos(latitudOrigenRAD) * Math.cos(latitudDestinoRAD) * Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c + 3.56;
    }
}
