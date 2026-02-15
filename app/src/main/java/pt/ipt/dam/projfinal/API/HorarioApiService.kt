package pt.ipt.dam.projfinal

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path


interface HorarioApiService {

    @GET("horarios")
    suspend fun getAllHorarios(): List<selecionar.Horario>

    @GET("horarios/{turma}")
    suspend fun getHorarioByTurma(@Path("turma") turma: String): selecionar.Horario
    @GET("turmas")
    fun getListaTurmas(): Call<List<String>>
}