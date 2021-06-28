package com.example.admin.eventappb.Remote;

import com.example.admin.eventappb.DTO.LocalizacionDTO;
import com.example.admin.eventappb.DTO.UsuarioDTO;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by JC on 6/12/2017.
 */

public interface SmsService {
    /*@Headers("Content-Type: application/json")*/
    @FormUrlEncoded
    @POST("usuario/pedir_sms")
    Call<UsuarioDTO> sent_sms(@Field("Nombre") String nombre,
                            @Field("Email") String email,
                            @Field("Mobile") String mobile);

    @FormUrlEncoded
    @POST("usuario/verificar_otp")
    Call<JsonObject> verify_opt(@Field("Otp") String otp);


}
