package com.example.admin.eventappb.DTO;

import android.graphics.Bitmap;

/**
 * Created by JC on 25/1/2018.
 */

public class ContactoDTO {
    String nombre;
    String email;
    String telefono;
    Bitmap thumb;
    Boolean checkedBox = false;
    Bitmap thumb_checked;

    public Bitmap getThumb_checked() {
        return thumb_checked;
    }

    public void setThumb_checked(Bitmap thumb_checked) {
        this.thumb_checked = thumb_checked;
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

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public Bitmap getThumb() {
        return thumb;
    }

    public void setThumb(Bitmap thumb) {
        this.thumb = thumb;
    }

    public Boolean getCheckedBox() {
        return checkedBox;
    }

    public void setCheckedBox(Boolean checkedBox) {
        this.checkedBox = checkedBox;
    }
}
