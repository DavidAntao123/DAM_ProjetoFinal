package pt.ipt.dam.projfinal.API

// Imports Retrofit para chamadas HTTP POST
import pt.ipt.dam.projfinal.API.Extras.*
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
     * POST http://10.0.2.2:3000/user/login
     *
     * @param request objeto LoginRequest com email e password
     * @return LoginResponse com dados do utilizador autenticado
     */

    @POST("user/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    /**
     * Endpoint de Registo.
     *
     * Envia email e password para a API Node.js através de POST.
     *
     * Exemplo:
     * POST http://10.0.2.2:3000/user/registo
     *
     * @param request objeto RegistarRquest com email e password
     * @return RegistarResponse com dados do utilizador registado
     */
    @POST("user/register")
    fun register(@Body request: RegistarRequest): Call<RegistarResponse>
}