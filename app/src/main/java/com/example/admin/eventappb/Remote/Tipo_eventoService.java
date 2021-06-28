package com.example.admin.eventappb.Remote;

import com.example.admin.eventappb.DTO.Tipo_eventoDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by JC on 6/12/2017.
 */

public interface Tipo_eventoService {
    @GET("tipo_evento/getAll")
    Call<List<Tipo_eventoDTO>> getAll();

}
