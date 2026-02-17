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

        /**
         * Quando o utilizador carrega no botão "Registar",
         * a activity de registar é aberta
         */
        btnRegistar.setOnClickListener {
            // Abre a activity de registo
            startActivity(Intent(this@Login, registar::class.java))
        }

        /**
         * Quando o utilizador carrega no botão "Registar",
         * a Main Activity é aberta
         */
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

        // Verifica se os campos não estão vazios antes de enviar para a API
        if (email.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
            return  // Interrompe a execução da função
        }

        // Cria objeto com os dados para enviar à API
        // A classe LoginRequest contém os campos email e password
        val request = LoginRequest(email, pass)


        // Chamada Retrofit
        // Faz uma chamada assíncrona à API de login
        // enqueue() executa a chamada em background sem bloquear a UI
        RetrofitClient.loginApi.login(request)
            .enqueue(object : Callback<LoginResponse> {

                //Chamado quando a API responde (com sucesso ou erro HTTP)
                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {
                    when {
                        //Login bem-sucedido
                        response.isSuccessful -> {
                            // Extrai o corpo da resposta (objeto LoginResponse)
                            val loginResponse = response.body()

                            if (loginResponse != null) {
                                // Guarda o email do utilizador nas preferências (sessão)
                                guardarSessao(loginResponse.email, "user")
                                Toast.makeText(this@Login, "Login OK!", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this@Login, MainActivity::class.java))
                                finish()
                            } else {
                            }
                        }
                        else -> {
                        }
                    }
                }

                /**
                 * Chamado quando ocorre uma falha na ligação à API
                 */
                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
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

    }
}
