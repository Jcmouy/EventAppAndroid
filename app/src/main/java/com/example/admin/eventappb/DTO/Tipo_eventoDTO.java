package com.example.admin.eventappb.DTO;

import com.example.admin.eventappb.R;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by JC on 23/9/2017.
 */

public class Tipo_eventoDTO {
    @SerializedName("ID")
    @Expose
    private int Id;
    @SerializedName("Nombre")
    @Expose
    private String nombre;

    public Tipo_eventoDTO(int id, String nombre) {
        this.Id = id;
        this.nombre = nombre;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        this.Id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
