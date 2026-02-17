

package pt.ipt.dam.projfinal.API

/**
 * LoginResponse
 *
 * Data class responsável por representar a resposta
 * recebida da API após uma tentativa de autenticação.
 *
 * Contém:
 * message → mensagem enviada pelo servidor
 *              (ex: "Login realizado com sucesso")
 * email → email do utilizador autenticado
 *
 * Esta classe é usada pelo Retrofit para converter
 * automaticamente a resposta JSON da API em objeto Kotlin.
 */
data class LoginResponse(
    val message: String,
    val email: String
)
