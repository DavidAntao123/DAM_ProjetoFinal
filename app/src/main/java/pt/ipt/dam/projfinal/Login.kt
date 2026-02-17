package pt.ipt.dam.projfinal

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import pt.ipt.dam.projfinal.API.*
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Liga esta Activity ao layout XML
        setContentView(R.layout.activity_login)

        // Associa os componentes do XML às variáveis Kotlin
        txtEmail = findViewById(R.id.txtEmail)
        txtPassword = findViewById(R.id.txtPassword)
        btnLogin = findViewById(R.id.btnLogin)


        /**
         * Quando o utilizador carrega no botão "Entrar",
         * é executada a função fazerLogin()
         */
        btnLogin.setOnClickListener {
            fazerLogin()
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
        if (email == "admin" && pass =="")
        {
            startActivity(Intent(this@Login, MainActivity::class.java))
            finish()
        }
/**
        // Chamada Retrofit
        RetrofitClient.loginApi.login(request)
            .enqueue(object : Callback<LoginResponse> {

                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {

                    // Login OK
                    if (response.isSuccessful) {

                        guardarSessao(response.body()!!.email)

                        // Abre MainActivity
                        startActivity(Intent(this@Login, MainActivity::class.java))
                        finish()

                    } else {
                        Toast.makeText(this@Login, "Login inválido", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Toast.makeText(this@Login, "Erro ligação API", Toast.LENGTH_SHORT).show()
                }
            })
        */
    }

     /**
     * Guarda o email do utilizador em SharedPreferences
     * Simula uma sessão simples local.
     */
    private fun guardarSessao(email: String) {
        val prefs = getSharedPreferences("user", MODE_PRIVATE)
        prefs.edit().putString("email", email).apply()
    }
}
