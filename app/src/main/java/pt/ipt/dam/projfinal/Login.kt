package pt.ipt.dam.projfinal

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import pt.ipt.dam.projfinal.API.Extras.LoginRequest
import pt.ipt.dam.projfinal.API.Extras.LoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Login
 *
 * Activity responsável pela autenticação do utilizador.
 *
 * Permite ao utilizador introduzir email e password.
 * Pode funcionar de duas formas:
 * Login local (modo teste)
 * Login através da API (Retrofit)
 */
class Login : AppCompatActivity() {
    // Campos do layout
    lateinit var txtEmail: EditText
    lateinit var txtPassword: EditText
    lateinit var btnLogin: Button
    private lateinit var btnRegistar: Button
    private lateinit var btnGuest: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Liga esta Activity ao layout XML
        setContentView(R.layout.activity_login)

        // Associa os componentes do XML às variáveis Kotlin
        txtEmail = findViewById(R.id.txtEmail)
        txtPassword = findViewById(R.id.txtPassword)
        btnLogin = findViewById(R.id.btnLogin)
        btnRegistar = findViewById(R.id.btnRegistar)
        btnGuest = findViewById(R.id.btnGuest)



        /**
         * Quando o utilizador carrega no botão "Entrar",
         * é executada a função fazerLogin()
         */
        btnLogin.setOnClickListener {
            fazerLogin()
        }

        btnRegistar.setOnClickListener {
            // Abre a activity de registo
            startActivity(Intent(this@Login, registar::class.java))
        }

        btnGuest.setOnClickListener {
            startActivity(Intent(this@Login, MainActivity::class.java))
            finish()
        }
    }

    /**
     * Função responsável por validar e processar o login
     */
    private fun fazerLogin() {
        // Obtém os valores introduzidos pelo utilizador
        val email = txtEmail.text.toString()
        val pass = txtPassword.text.toString()

        // Cria objeto para enviar à API
        val request = LoginRequest(email, pass)

        // Chamada Retrofit
        RetrofitClient.loginApi.login(request)
            .enqueue(object : Callback<LoginResponse> {

                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {

                    println("========== RESPOSTA RECEBIDA ==========")
                    println("5. Código HTTP: ${response.code()}")
                    println("6. Mensagem: ${response.message()}")
                    println("7. Headers: ${response.headers()}")

                    // Tenta ler o corpo da resposta
                    try {
                        if (response.isSuccessful) {
                            val body = response.body()
                            println("8. Corpo sucesso: $body")
                        } else {
                            val errorBody = response.errorBody()?.string()
                            println("8. Corpo erro: $errorBody")
                        }
                    } catch (e: Exception) {
                        println("8. Erro ao ler corpo: ${e.message}")
                    }

                        guardarSessao(response.body()!!.email)

                    when {
                        response.isSuccessful -> {
                            val loginResponse = response.body()
                            if (loginResponse != null) {
                                guardarSessao(loginResponse.email, "user")
                                Toast.makeText(this@Login, "Login OK!", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this@Login, MainActivity::class.java))
                                finish()
                            } else {
                            }
                        }
                        response.code() == 404 -> {
                            // Mostra mensagem mais detalhada
                            Toast.makeText(
                                this@Login,
                                "Email não registado. Verifique se:\n" +
                                        "1. O email '$email' existe na BD\n" +
                                        "2. O servidor está em ${RetrofitClient.BASE_URL}\n" +
                                        "3. A rota é /user/login ou /users/login?",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        else -> {
                        }
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {

                    println("========== ERRO DE LIGAÇÃO ==========")
                    println("Erro: ${t.message}")
                    println("Causa: ${t.cause}")
                    t.printStackTrace()
                    println("======================================")

                    Toast.makeText(
                        this@Login,
                        "Erro de ligação:\n${t.message}\nVerifique IP: ${RetrofitClient.BASE_URL}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })

    }

     /**
     * Guarda o email do utilizador em SharedPreferences
     * Simula uma sessão simples local.
     */
    private fun guardarSessao(email: String, tipo: String = "user") {
        val prefs = getSharedPreferences("user", MODE_PRIVATE)
        prefs.edit().apply {
            putString("email", email)
            putString("tipo", tipo)
            apply()
        }

        // DEBUG: confirmar que guardou
        println("Email guardado: $email, Tipo: $tipo")
    }
}
