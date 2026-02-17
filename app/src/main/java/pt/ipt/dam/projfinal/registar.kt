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
        initViews()

        // Configura os listeners
        setupListeners()
    }

    /**
     * Inicializa as views do layout
     */
    private fun initViews() {
        txtEmail = findViewById(R.id.txtEmail)
        txtPassword = findViewById(R.id.txtPassword)
        txtConfirmarPassword = findViewById(R.id.txtConfirmarPassword)
        btnRegistar = findViewById(R.id.btnRegistar)
        txtLogin = findViewById(R.id.txtLogin)
    }

    /**
     * Configura os listeners dos botões
     */
    private fun setupListeners() {
        btnRegistar.setOnClickListener {
            registarUtilizador()
        }

        txtLogin.setOnClickListener {
            // Volta para o ecrã de login
            finish()
        }
    }

    /**
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

        // Mostra loading no botão
        mostrarLoading(true)

        // Cria objeto para enviar à API
        val request = RegistarRequest(email, password)

        // Chamada Retrofit
        RetrofitClient.loginApi.register(request)
            .enqueue(object : Callback<RegistarResponse> {
                override fun onResponse(
                    call: Call<RegistarResponse>,
                    response: Response<RegistarResponse>
                ) {
                    mostrarLoading(false)

                    println("========== RESPOSTA REGISTO ==========")
                    println("Código HTTP: ${response.code()}")

                    try {
                        if (response.isSuccessful) {
                            val registoResponse = response.body()
                            println("Sucesso: $registoResponse")

                            Toast.makeText(
                                this@registar,
                                "Conta criada com sucesso!",
                                Toast.LENGTH_SHORT
                            ).show()

                            // Volta para o ecrã de login
                            finish()

                        } else {
                            val errorBody = response.errorBody()?.string()
                            println("Erro: $errorBody")

                            when (response.code()) {
                                400 -> mostrarErro("Email já registado")
                                409 -> mostrarErro("Email já existe na base de dados")
                                else -> mostrarErro("Erro no servidor: ${response.code()}")
                            }
                        }
                    } catch (e: Exception) {
                        println("Erro ao processar resposta: ${e.message}")
                        mostrarErro("Erro ao processar resposta")
                    }
                }

                override fun onFailure(call: Call<RegistarResponse>, t: Throwable) {
                    mostrarLoading(false)

                    println("========== ERRO LIGAÇÃO REGISTO ==========")
                    println("Erro: ${t.message}")
                    t.printStackTrace()

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
            mostrarErro("Preencha todos os campos")
            return false
        }

        // Valida formato do email
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mostrarErro("Email inválido")
            return false
        }

        // Valida tamanho da password
        if (password.length < 4) {
            mostrarErro("A password deve ter pelo menos 4 caracteres")
            return false
        }

        // Valida se as passwords coincidem
        if (password != confirmarPassword) {
            mostrarErro("As passwords não coincidem")
            return false
        }

        return true
    }

    /**
     * Controla o estado de loading do botão
     */
    private fun mostrarLoading(isLoading: Boolean) {
        btnRegistar.isEnabled = !isLoading
        btnRegistar.text = if (isLoading) "A registar..." else "Registar"
    }

    /**
     * Mostra mensagem de erro em Toast
     */
    private fun mostrarErro(mensagem: String) {
        Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show()
    }
}