package com.rlmonsalve.pokemonmovil;

/**
 * Created by ROBERTO on 20/09/2016.
 */
public class Posicion {

    private Double latitud;
    private Double longitud;

    public Posicion(String latitud, String longitud) {
        this.latitud= Double.valueOf(latitud);
        this.longitud= Double.valueOf(longitud);
    }

    public Double getLatitud() {
        return latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = Double.valueOf(latitud);
    }

    public Double getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = Double.valueOf(longitud);
    }
}
