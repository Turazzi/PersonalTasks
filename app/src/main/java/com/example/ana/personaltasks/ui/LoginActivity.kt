package com.example.ana.personaltasks.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ana.personaltasks.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private val binding: ActivityLoginBinding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }

    //variável que tem a ferramenta principal para todas as opções de autenticação do firebase
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        // verifica se já tem um usuário válido guardado
        if(auth.currentUser != null) {
            //se tiver, usuário ja fez login antes que nao foi expirado - nao precisa mostrar a tela de login, vai direto pra main
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        binding.loginBt.setOnClickListener {
            val email = binding.emailEt.text.toString().trim()
            val password = binding.passwordEt.text.toString().trim()
            //verifica se email e senha nao estao vazios
            if (email.isNotEmpty() && password.isNotEmpty()) {
                //envia as credenciais para os servidores do firebase para verificação
                auth.signInWithEmailAndPassword(email, password)
                    //tratamento de resposta
                    .addOnCompleteListener(this) { task ->
                        //se o firebase responder que o login deu certo, ele vai para a main
                        if(task.isSuccessful) {
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        }
                        else {
                            Toast.makeText(baseContext, "Falha na autenticação.", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
            else {
                Toast.makeText(baseContext, "Preencha todos os campos.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.registerBt.setOnClickListener {
            val email = binding.emailEt.text.toString().trim()
            val password = binding.passwordEt.text.toString().trim()
            if (email.isNotEmpty() && password.isNotEmpty()) {
                //pede para o firebase criar uma nova conta de usuário
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(baseContext, "Usuário registrado com sucesso.", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(baseContext, "Falha no registro: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                        }
                    }
            } else {
                Toast.makeText(baseContext, "Preencha todos os campos.", Toast.LENGTH_SHORT).show()
            }
        }
    }

}