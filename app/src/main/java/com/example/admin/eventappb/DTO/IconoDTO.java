package com.example.admin.eventappb.DTO;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.DrawableUtils;

import com.example.admin.eventappb.R;

/**
 * Created by JC on 23/9/2017.
 */

public class IconoDTO {
    private String nombre;
    private int idDrawable;

    public IconoDTO(String nombre, int idDrawable) {
        this.nombre = nombre;
        this.idDrawable = idDrawable;
    }

    public IconoDTO() {

    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getIdDrawable() {
        return idDrawable;
    }

    public void setIdDrawable(int idDrawable) {
        this.idDrawable = idDrawable;
    }

    public int getId() {
        return nombre.hashCode();
    }

    public static IconoDTO[] ITEMS = {
            new IconoDTO("Icono1", R.drawable.image1),
            new IconoDTO("Icono2", R.drawable.image2),
            new IconoDTO("Icono3", R.drawable.image3),
            new IconoDTO("Icono4", R.drawable.image4),
            new IconoDTO("Icono5", R.drawable.image5),
            new IconoDTO("Icono6", R.drawable.image6),
            new IconoDTO("Icono7", R.drawable.image7),
            new IconoDTO("Icono8", R.drawable.image8),
            new IconoDTO("Icono9", R.drawable.image9),
            new IconoDTO("Icono10", R.drawable.image10),
            new IconoDTO("Icono11", R.drawable.image11),
            new IconoDTO("Icono12", R.drawable.image12),

    };

    /**
     * Obtiene item basado en su identificador
     *
     * @param id identificador
     * @return Coche
     */
    public IconoDTO getItem(int id) {
        for (IconoDTO item : ITEMS) {
            if (item.getIdDrawable() == id) {
                return item;
            }
        }
        return null;
    }

}
