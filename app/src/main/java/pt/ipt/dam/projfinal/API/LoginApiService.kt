package pt.ipt.dam.projfinal.API

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Interface Retrofit para chamadas de LOGIN
 */
interface LoginApiService {
    // POST http://10.0.2.2:3000/login/login
    @POST("login/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>
}