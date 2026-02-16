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
 * Activity responsável pelo login do utilizador
 */
class Login : AppCompatActivity() {

    lateinit var txtEmail: EditText
    lateinit var txtPassword: EditText
    lateinit var btnLogin: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Liga os campos do XML ao Kotlin
        txtEmail = findViewById(R.id.txtEmail)
        txtPassword = findViewById(R.id.txtPassword)
        btnLogin = findViewById(R.id.btnLogin)

        // Botão Entrar
        btnLogin.setOnClickListener {
            fazerLogin()
        }
    }

    /**
     * Função chamada ao carregar no botão Entrar
     */
    private fun fazerLogin() {

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
    }

    /**
     * Guarda email em SharedPreferences
     */
    private fun guardarSessao(email: String) {
        val prefs = getSharedPreferences("user", MODE_PRIVATE)
        prefs.edit().putString("email", email).apply()
    }
}
