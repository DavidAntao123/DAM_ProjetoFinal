package pt.ipt.dam.projfinal.API

// Imports Retrofit para chamadas HTTP POST
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * LoginApiService
 *
 * Interface Retrofit responsável pelas chamadas de autenticação.
 *
 * Define as rotas relacionadas com login do utilizador.
 * Retrofit cria automaticamente a implementação desta interface.
 */
interface LoginApiService {
    /**
     * Endpoint de login.
     *
     * Envia email e password para a API Node.js através de POST.
     *
     * Exemplo:
     * POST http://10.0.2.2:3000/login/login
     *
     * @param request objeto LoginRequest com email e password
     * @return LoginResponse com dados do utilizador autenticado
     */

    @POST("login/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>
}