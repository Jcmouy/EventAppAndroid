package com.example.admin.eventappb.Remote;

import android.support.annotation.Nullable;

import com.example.admin.eventappb.DTO.EventoDTO;
import com.example.admin.eventappb.DTO.IconoDTO;
import com.example.admin.eventappb.DTO.Tipo_eventoDTO;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by JC on 6/12/2017.
 */

public interface EventoService {
    /*@Headers("Content-Type: application/json")*/
    @FormUrlEncoded
    @POST("evento/insert")
    Call<EventoDTO> insert(@Field("Nombre") String nombre,
                           @Field("Descripcion") String descripcion,
                           @Field("Tipo_evento") long tipo_evento,
                           @Field("Subtipo_evento") String subtipo_evento,
                           @Field("Visibilidad") boolean visibilidad,
                           @Field("Icono") int icono,
                           @Field("Imagen_profile") String imagen_profile,
                           @Field("Video_background") String video_background,
                           @Field("Usuario_mobile") String usuario_mobile,
                           @Field("Status") boolean status);

    @GET("evento/get/{id}")
    Call<JsonObject> get(@Path("id") String id);

    //void insert(@Body RequestBody body, Callback<Response<PersonDTO>> callback);

    /*
    @POST("/reloj/marca/ultima")
    void last(@Body RequestBody body, Callback<Response<PersonDTO>> callback);

    @POST("/reloj/marca/consulta")
    void getByFilters(@Body RequestBody body, Callback<List<PersonDTO>> callback);

    @POST("/reloj/marca/insertoffline")
    void syncAll(@Body RequestBody body, Callback<Response<PersonDTO>> callback);
    */
}
