

package pt.ipt.dam.projfinal.API

/**
 * Resposta recebida da API depois do login
 */
data class LoginResponse(
    val message: String,
    val email: String
)
