package br.edu.ifsp.scl.ads.intents

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import br.edu.ifsp.scl.ads.intents.Constants.Companion.PARAMETRO_EXTRA
import br.edu.ifsp.scl.ads.intents.databinding.ActivityParametroBinding

class ParametroActivity : AppCompatActivity(), Constants {

    private val apb : ActivityParametroBinding by lazy {
        ActivityParametroBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(apb.root)

        intent.getStringExtra(PARAMETRO_EXTRA)?.let { params ->
            apb.parametroEt.setText(params)
        }

        apb.enviarParametroBt.setOnClickListener {
            val returnIntent = Intent()
            returnIntent.putExtra(PARAMETRO_EXTRA, apb.parametroEt.text.toString())
            setResult(RESULT_OK, returnIntent)
            finish()
        }
    }
}