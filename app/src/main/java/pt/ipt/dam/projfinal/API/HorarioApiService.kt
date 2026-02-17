package pt.ipt.dam.projfinal

// Imports necessários para chamadas HTTP com Retrofit
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path


/**
 * HorarioApiService
 *
 * Interface Retrofit responsável por definir todos os endpoints
 * relacionados com horários
 *
 * Cada função corresponde a uma rota da API Node.js
 * Retrofit gera automaticamente a implementação destas chamadas.
 */
interface HorarioApiService {

    /**
     * Obtém a lista de cursos disponíveis
     * Utilizado para preencher campos AutoCompleteTextView.
     *
     * Exemplo de rota:
     * GET http://10.0.2.2:3000/horarios/cursos
     */
    @GET("horarios/cursos")
    fun getCursos(): Call<List<String>>

    /**
     * Obtém a lista de salas disponíveis.
     * Usado para seleção de salas antes de carregar horários.
     *
     */
    @GET("horarios/sala/salaNomes")
    fun getSalas(): Call<List<String>>

    /**
     * Obtém o horário completo de uma turma específica.
     *
     * Exemplo:
     * GET http://10.0.2.2:3000/horarios/LEI3
     *
     * @param turma código da turma (ex: LEI3)
     * @return objeto HorarioResponse com os dados do horário
     */
    @GET("horarios/{turma}")
    suspend fun getHorarioByTurma(@Path("turma") turma: String): HorarioResponse

    /**
     * Obtém o horário através do nome da sala.
     *
     * Exemplo:
     * GET http://10.0.2.2:3000/horarios/sala/I152
     *
     * @param sala nome da sala
     * @return HorarioResponse
     */
    @GET("horarios/sala/{sala}")
    suspend fun getHorarioBySala(@Path("sala") turma: String): HorarioResponse
}