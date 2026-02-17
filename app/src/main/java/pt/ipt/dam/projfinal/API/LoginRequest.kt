

package pt.ipt.dam.projfinal.API


/**
 * LoginRequest
 *
 * Data class responsável por representar os dados enviados
 * para a API durante o processo de autenticação.
 *
 * Contém:
 * email do utilizador
 * password introduzida pelo utilizador
 *
 * Este objeto é convertido automaticamente em JSON pelo Retrofit
 * antes de ser enviado para o servidor Node.js
 */
data class LoginRequest(
    val email: String,
    val password: String
)
