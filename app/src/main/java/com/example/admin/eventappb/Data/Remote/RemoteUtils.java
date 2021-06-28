package com.example.admin.eventappb.Data.Remote;

import com.example.admin.eventappb.Remote.EventoService;
import com.example.admin.eventappb.Remote.LocalizacionService;
import com.example.admin.eventappb.Remote.RetrofitClient;
import com.example.admin.eventappb.Remote.SmsService;
import com.example.admin.eventappb.Remote.Tipo_eventoService;
import com.example.admin.eventappb.Remote.UsuarioService;

/**
 * Created by JC on 9/12/2017.
 */

public class RemoteUtils {

    private RemoteUtils(){}

    //public static String BASE_URL = "http://10.0.2.2:3000/";

    public static String BASE_URL = "http://192.168.1.11:3000/";

    public static Tipo_eventoService getTipo_eventoService(){
        return RetrofitClient.getClient(BASE_URL).create(Tipo_eventoService.class);
    }

    public static EventoService getEventoService(){
        return RetrofitClient.getClient(BASE_URL).create(EventoService.class);
    }

    public static LocalizacionService getLocalizacionService(){
        return RetrofitClient.getClient(BASE_URL).create(LocalizacionService.class);
    }

    public static SmsService getSmsService(){
        return RetrofitClient.getClient(BASE_URL).create(SmsService.class);
    }

    public static UsuarioService getUsuarioService(){
        return RetrofitClient.getClient(BASE_URL).create(UsuarioService.class);
    }
}
