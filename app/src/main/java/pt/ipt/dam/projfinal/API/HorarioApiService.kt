package pt.ipt.dam.projfinal.API


import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path




interface HorarioApiService {

    @GET("horarios")
    suspend fun getALL(): List<Horario>

    @GET("horarios/{turma}")
    suspend fun getHorario(@Path("turma") turma: String): Horario
}

