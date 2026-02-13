package pt.ipt.dam.projfinal

// Imports necessários para navegar entre Activities, usar Toasts e componentes UI
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

/**
 * Login Activity
 * Este ecrã permite ao utilizador autenticar-se antes de entrar na aplicação.
 * Contém:
 * Campo Email
 * Campo Password
 * Botão Entrar
 *
 * Se a password estiver correta, abre o MainActivity
 */
class Login : AppCompatActivity() {

    /**
     * Método chamado quando esta Activity é criada
     * Aqui ligamos o layout XML e configuramos o botão de login
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Liga esta Activity ao layout activity_login.xml
        setContentView(R.layout.activity_login)

        // Referências aos componentes do layout através dos IDs
        val edtEmail = findViewById<EditText>(R.id.edtEmail)
        val edtPassword = findViewById<EditText>(R.id.edtPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)

        /**
         * Clique no botão Login
         * Vai validar os campos e autenticar o utilizador
         */
        btnLogin.setOnClickListener {

            // Vai buscar o texto introduzido pelo utilizador
            val email = edtEmail.text.toString()
            val password = edtPassword.text.toString()

            // Verifica se algum campo está vazio
            if (email.isEmpty() || password.isEmpty()) {

                // Mostra mensagem de erro
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()

            } else {

                /**
                 * Autenticação simples
                 * Apenas aceita estas passwords:
                 * Email: admin@ipt.pt
                 * Password: 1234
                 */
                if (email == "admin@ipt.pt" && password == "1234") {

                    // Mensagem de sucesso
                    Toast.makeText(this, "Login com sucesso", Toast.LENGTH_SHORT).show()

                    // Cria Intent para abrir o menu principal (MainActivity)
                    val intent = Intent(this, MainActivity::class.java)

                    // Abre o MainActivity
                    startActivity(intent)

                    // Fecha esta Activity para não voltar atrás
                    finish()

                } else {

                    // Caso a password esteja errada
                    Toast.makeText(this, "Password inválida", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}


