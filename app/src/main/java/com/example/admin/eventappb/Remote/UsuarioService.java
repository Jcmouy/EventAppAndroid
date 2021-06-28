package com.example.admin.eventappb.Remote;

import com.example.admin.eventappb.DTO.UsuarioDTO;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by JC on 6/12/2017.
 */

public interface UsuarioService {

    // GET usuario Status,Premium by mobile
    @GET("usuario/get_mobile/{mobile}")
    Call<JsonObject> getStatusPremium(@Path("mobile") String mobile);

    // GET check if user has an active public event
    @GET("usuario/get_evento/{mobile}")
    Call<JsonObject> checkPublicEvent(@Path("mobile") String mobile);

}
