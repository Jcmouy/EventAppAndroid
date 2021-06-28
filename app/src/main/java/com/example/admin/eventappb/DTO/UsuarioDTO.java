package com.example.admin.eventappb.DTO;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by JC on 16/2/2018.
 */

public class UsuarioDTO {
    @SerializedName("ID")
    @Expose
    private int id;
    @SerializedName("Nombre")
    @Expose
    private String nombre;
    @SerializedName("Email")
    @Expose
    private String email;
    @SerializedName("Mobile")
    @Expose
    private String mobile;
    @SerializedName("Api_key")
    @Expose
    private String api_key;
    @SerializedName("Status")
    @Expose
    private boolean status;
    @SerializedName("Fecha_creado")
    @Expose
    private Date fecha_creado;
    @SerializedName("Hash_password")
    @Expose
    private String hash_password;
    @SerializedName("Premium")
    @Expose
    private boolean premium;

    public UsuarioDTO(int id, String nombre, String email, String mobile, String api_key,
                      boolean status, Date fecha_creado, String hash_password, boolean premium) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.mobile = mobile;
        this.api_key = api_key;
        this.status = status;
        this.fecha_creado = fecha_creado;
        this.hash_password = hash_password;
        this.premium = premium;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getApi_key() {
        return api_key;
    }

    public void setApi_key(String api_key) {
        this.api_key = api_key;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Date getFecha_creado() {
        return fecha_creado;
    }

    public void setFecha_creado(Date fecha_creado) {
        this.fecha_creado = fecha_creado;
    }

    public String getHash_password() {
        return hash_password;
    }

    public void setHash_password(String hash_password) {
        this.hash_password = hash_password;
    }

    public boolean isPremium() {
        return premium;
    }

    public void setPremium(boolean premium) {
        this.premium = premium;
    }
}
