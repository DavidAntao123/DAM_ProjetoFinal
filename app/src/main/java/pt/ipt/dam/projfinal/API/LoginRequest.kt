

package pt.ipt.dam.projfinal.API

/**
 * Dados enviados para a API no login
 */
data class LoginRequest(
    val email: String,
    val password: String
)
