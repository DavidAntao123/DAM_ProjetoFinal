package pt.ipt.dam.projfinal

// Imports Retrofit e conversor Gson (JSON <-> Kotlin)
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import pt.ipt.dam.projfinal.API.*




/**
 * RetrofitClient
 *
 * Objeto singleton responsável por configurar e fornecer
 * uma instância do Retrofit
 *
 * Define o endereço base da API e o conversor JSON
 * Garante que apenas uma instância é criada durante toda a aplicação
 */
object RetrofitClient {
    /**
     * Endereço base da API.
     * Pode ser:
     * 10.0.2.2 → emulador Android
     * IP local → telemóvel físico
     */
    const val BASE_URL = "http://10.10.208.71:3000/"
    /**
     * Instância da interface HorarioApiService e LoginApiService.
     * Criada apenas quando for utilizada pela primeira vez (lazy).
     */

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    /**
     * Instância da interface HorarioApiService.
     * Responsável por todas as chamadas relacionadas com horários e salas.
     * Criada apenas quando for utilizada pela primeira vez (lazy initialization).
     */
    val horarioApi: HorarioApiService by lazy {
        retrofit.create(HorarioApiService::class.java)
    }
    /**
     * Instância da interface LoginApiService.
     * Responsável por todas as chamadas relacionadas com autenticação (login/registo).
     * Criada apenas quando for utilizada pela primeira vez (lazy initialization).
     */
    val loginApi: LoginApiService by lazy {
        retrofit.create(LoginApiService::class.java)
    }
}