package pt.ipt.dam.projfinal

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import pt.ipt.dam.projfinal.API.*
import pt.ipt.dam.projfinal.API.Extras.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Activity responsável pelo registo de novos utilizadores
 */
class registar : AppCompatActivity() {

    // UI Elements
    private lateinit var txtEmail: EditText
    private lateinit var txtPassword: EditText
    private lateinit var txtConfirmarPassword: EditText
    private lateinit var btnRegistar: Button
    private lateinit var txtLogin: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registar)

        // Inicializa as views
        txtEmail = findViewById(R.id.txtEmail)
        txtPassword = findViewById(R.id.txtPassword)
        txtConfirmarPassword = findViewById(R.id.txtConfirmarPassword)
        btnRegistar = findViewById(R.id.btnRegistar)
        txtLogin = findViewById(R.id.txtLogin)

        // Configura os listeners
        btnRegistar.setOnClickListener {
            registarUtilizador()
        }

        txtLogin.setOnClickListener {
            // Volta para o ecrã de login
            finish()
        }
    }


    /*
     * Função chamada ao carregar no botão Registar
     */
    private fun registarUtilizador() {
        val email = txtEmail.text.toString().trim()
        val password = txtPassword.text.toString().trim()
        val confirmarPassword = txtConfirmarPassword.text.toString().trim()

        // Validações
        if (!validarCampos(email, password, confirmarPassword)) {
            return
        }

        // Cria objeto para enviar à API
        val request = RegistarRequest(email, password)

        // Chamada Retrofit
        // Faz a chamada à API de registo usando Retrofit
        // enqueue() executa a chamada de forma assíncrona (em background)
        RetrofitClient.loginApi.register(request)
            .enqueue(object : Callback<RegistarResponse> {
                override fun onResponse(
                    call: Call<RegistarResponse>,
                    response: Response<RegistarResponse>
                ) {
                    // Bloco try-catch para capturar erros inesperados
                    try {
                        // Verifica se a resposta foi bem-sucedida
                        if (response.isSuccessful) {
                            val registoResponse = response.body()

                            // Volta para o ecrã de login
                            finish()

                        } else {
                            val errorBody = response.errorBody()?.string()
                            println("Erro: $errorBody")

                        }
                    } catch (e: Exception) {
                        mostrarErro("Erro ao processar resposta")
                    }
                }
                // Chamado quando ocorre uma falha na ligação à API
                override fun onFailure(call: Call<RegistarResponse>, t: Throwable) {
                    Toast.makeText(
                        this@registar,
                        "Erro de ligação: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    /**
     * Valida os campos do formulário
     */
    private fun validarCampos(email: String, password: String, confirmarPassword: String): Boolean {
        // Verifica campos vazios
        if (email.isEmpty() || password.isEmpty() || confirmarPassword.isEmpty()) {
            mostrarErro(getString(R.string.registar_erro_PrencherCampos))
            return false
        }

        // Valida formato do email
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mostrarErro(getString(R.string.registar_erro_EmailErrado))
            return false
        }

        // Valida tamanho da password
        if (password.length < 4) {
            mostrarErro(getString(R.string.registar_erro_Password))
            return false
        }

        // Valida se as passwords coincidem
        if (password != confirmarPassword) {
            mostrarErro(getString(R.string.registar_erro_PasswordDiferente))
            return false
        }

        return true
    }
    /**
     * Mostra mensagem de erro em Toast
     */
    private fun mostrarErro(mensagem: String) {
        Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show()
    }
}