package pt.ipt.dam.projfinal.API
//import pt.ipt.dam.projfinal.API.HorarioApiService
import pt.ipt.dam.projfinal.API.LoginApiService

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Classe singleton que cria a ligação à API Node.js
 */
object RetrofitClient {

    private const val BASE_URL = "http://10.0.2.2:3000/"

    // Instância Retrofit criada apenas uma vez
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL) // URL base
            .addConverterFactory(GsonConverterFactory.create()) // JSON -> Kotlin
            .build()
    }

       val horarioApi: HorarioApiService by lazy {
       retrofit.create(HorarioApiService::class.java)
    }

    // Serviço de LOGIN
    val loginApi: LoginApiService by lazy {
        retrofit.create(LoginApiService::class.java)
    }
}
