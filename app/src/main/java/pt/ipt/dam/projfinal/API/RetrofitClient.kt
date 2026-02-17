package pt.ipt.dam.projfinal

// Imports Retrofit e conversor Gson (JSON <-> Kotlin)
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


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
    private const val BASE_URL = "http://172.20.10.6:3000/"
    /**
     * Instância da interface HorarioApiService.
     * Criada apenas quando for utilizada pela primeira vez (lazy).
     */
    val instance: HorarioApiService by lazy {
        Retrofit.Builder()
            // Define o endereço base da API
            .baseUrl(BASE_URL)
            // Conversor Gson para transformar JSON em objetos Kotlin
            .addConverterFactory(GsonConverterFactory.create())
            // Cria o Retrofit
            .build()
            // Liga Retrofit à interface HorarioApiService
            .create(HorarioApiService::class.java)
    }
}