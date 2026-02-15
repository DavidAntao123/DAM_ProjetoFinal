package pt.ipt.dam.projfinal

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path


interface HorarioApiService {

    @GET("horarios")
    suspend fun getALL(): List<selecionar.Horario>

    @GET("horarios/{turma}")
    suspend fun getHorario(@Path("turma") turma: String): selecionar.Horario

    @GET("horarios/cursos")
    fun getCursos(): Call<List<String>>
}

