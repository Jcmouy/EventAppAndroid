package com.example.admin.eventappb.DTO;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by JC on 21/10/2017.
 */

public class EventoDTO {
    @SerializedName("ID")
    @Expose
    private int id;
    @SerializedName("Nombre")
    @Expose
    private String nombre;
    @SerializedName("Descripcion")
    @Expose
    private String descripcion;
    @SerializedName("Tipo_evento")
    @Expose
    private Tipo_eventoDTO tipo_evento;
    @SerializedName("Subtipo_evento")
    @Expose
    private String subtipo_evento;
    @SerializedName("Visibilidad")
    @Expose
    private boolean visibilidad;
    @SerializedName("Icono")
    @Expose
    private int icono;
    @SerializedName("Imagen_profile")
    @Expose
    private String imagen_profile;
    @SerializedName("Video_background")
    @Expose
    private String video_background;
    @SerializedName("Usuario_mobile")
    @Expose
    private String usuario_mobile;
    @SerializedName("Status")
    @Expose
    private boolean status;

    public EventoDTO(String nombre, String descripcion, Tipo_eventoDTO tipo_evento, String subtipo_evento, boolean visibilidad, int icono,
                     String imagen_profile, String video_background, String usuario_mobile, boolean status) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.tipo_evento = tipo_evento;
        this.subtipo_evento = subtipo_evento;
        this.visibilidad = visibilidad;
        this.icono = icono;
        this.imagen_profile = imagen_profile;
        this.video_background = video_background;
        this.usuario_mobile = usuario_mobile;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Tipo_eventoDTO getTipo_evento() {
        return tipo_evento;
    }

    public void setTipo_evento(Tipo_eventoDTO tipo_evento) {
        this.tipo_evento = tipo_evento;
    }

    public String getSubtipo_evento() {
        return subtipo_evento;
    }

    public void setSubtipo_evento(String subtipo_evento) {
        this.subtipo_evento = subtipo_evento;
    }

    public boolean isVisibilidad() {
        return visibilidad;
    }

    public void setVisibilidad(boolean visibilidad) {
        this.visibilidad = visibilidad;
    }

    public int getIcono() {
        return icono;
    }

    public void setIcono(int icono) {
        this.icono = icono;
    }

    public String getImagen_profile() {
        return imagen_profile;
    }

    public void setImagen_profile(String imagen_profile) {
        this.imagen_profile = imagen_profile;
    }

    public String getVideo_background() {
        return video_background;
    }

    public void setVideo_background(String video_background) {
        this.video_background = video_background;
    }

    public String getUsuario_mobile() {
        return usuario_mobile;
    }

    public void setUsuario_mobile(String usuario_mobile) {
        this.usuario_mobile = usuario_mobile;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
