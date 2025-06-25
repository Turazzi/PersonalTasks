package com.example.ana.personaltasks.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ana.personaltasks.R
import com.example.ana.personaltasks.databinding.ActivityAboutBinding

class AboutActivity : AppCompatActivity() {

    private val aab : ActivityAboutBinding by lazy {
        ActivityAboutBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(aab.root)

        val toolbarIcon = findViewById<ImageView>(R.id.toolbar_icon)
        toolbarIcon.visibility = View.GONE

        with(aab) {
            cancelarBtn.setOnClickListener {
                finish()

            }

            discarBtn.setOnClickListener {

                val numero = aab.dadoParaEnviarEt.text.toString().trim()

                if (numero.isNotEmpty()) {
                    val uri = Uri.parse("tel:$numero")

                    val intent = Intent(Intent.ACTION_DIAL, uri)
                    startActivity(intent)
                }

            }

            navegadorBtn.setOnClickListener {

                var url = aab.dadoParaEnviarEt.text.toString().trim()

                if (url.isNotEmpty()) {
                    if(!url.startsWith("https://") && !url.startsWith("http://")) {
                        url = "http://$url"
                    }

                    val uri = Uri.parse(url)
                    val intent = Intent(Intent.ACTION_VIEW, uri)
                    startActivity(intent)
                }

            }

            compartilharBtn.setOnClickListener {

                val texto = aab.dadoParaEnviarEt.text.toString().trim()

                if(texto.isNotEmpty()) {

                    val intent = Intent(Intent.ACTION_SEND)

                    intent.type = "text/plain"
                    intent.putExtra(Intent.EXTRA_TEXT, texto)

                    val chooser = Intent.createChooser(intent, "Compartilhar texto com...")
                    startActivity(chooser)

                }

            }
        }

    }
}