package pt.ipt.dam.projfinal

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface HorarioApiService {

    // Obtém a lista de strings para o AutoComplete
    @GET("horarios/cursos")
    fun getCursos(): Call<List<String>>

    // Obtém o objeto completo do horário de uma turma específica
    // Se a tua rota no Node é router.get('/:turma'), o código abaixo está correto
    @GET("horarios/{turma}")
    suspend fun getHorario(@Path("turma") turma: String): HorarioResponse
}