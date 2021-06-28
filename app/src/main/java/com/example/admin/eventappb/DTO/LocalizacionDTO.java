package com.example.admin.eventappb.DTO;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by JC on 2/2/2018.
 */

public class LocalizacionDTO {
    @SerializedName("ID")
    @Expose
    private int id;
    @SerializedName("Lat")
    @Expose
    private double lat;
    @SerializedName("Long")
    @Expose
    private double longitud;
    @SerializedName("Evento")
    @Expose
    private EventoDTO evento;

    public LocalizacionDTO(int id, double lat, double longitud, EventoDTO evento) {
        this.id = id;
        this.lat = lat;
        this.longitud = longitud;
        this.evento = evento;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public EventoDTO getEvento() {
        return evento;
    }

    public void setEvento(EventoDTO evento) {
        this.evento = evento;
    }
}
