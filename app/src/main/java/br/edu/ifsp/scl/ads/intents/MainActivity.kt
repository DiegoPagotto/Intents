package br.edu.ifsp.scl.ads.intents

import android.Manifest.permission.CALL_PHONE
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import br.edu.ifsp.scl.ads.intents.Constants.Companion.PARAMETRO_EXTRA
import br.edu.ifsp.scl.ads.intents.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), Constants {

    private val amb : ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private lateinit var parl : ActivityResultLauncher<Intent>
    private lateinit var permissionCallARL : ActivityResultLauncher<String>
    private lateinit var getImageARL : ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(amb.root)

        parl = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result?.resultCode == RESULT_OK) {
                val returnValue = result.data?.getStringExtra(PARAMETRO_EXTRA) ?: ""
                amb.parametroTv.text = returnValue
            }
        }

        amb.entrarParametroBt.setOnClickListener {
            val intent = Intent("DIEGUETE")
            intent.putExtra(PARAMETRO_EXTRA, amb.parametroTv.text.toString())
            parl.launch(intent)
        }

        permissionCallARL = registerForActivityResult(ActivityResultContracts.RequestPermission()) { permission ->
            if (permission) {
                callNumber(true)
            } else{
                Toast.makeText(this, "Permissão negada", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        getImageARL = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result?.resultCode == RESULT_OK) {
                val imageURI = result.data?.data
                imageURI?.let {
                    amb.parametroTv.text = it.toString()
                    val viewImage = Intent(Intent.ACTION_VIEW, it)
                    startActivity(viewImage)
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.viewMi -> {
                val url : Uri = Uri.parse(amb.parametroTv.text.toString())
                val intent = Intent(Intent.ACTION_VIEW, url)
                startActivity(intent)
                true
            }
            R.id.callMi -> {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if(checkSelfPermission(CALL_PHONE) == PERMISSION_GRANTED) {
                        callNumber(true)
                    } else {
                        permissionCallARL.launch(CALL_PHONE)
                    }
                } else {
                    callNumber(true)
                }
                true
            }
            R.id.dialMi -> {
                callNumber(false)
                true
            }
            R.id.pickMi -> {
                val getImageIntent: Intent = Intent(Intent.ACTION_PICK)
                val imageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).path
                getImageIntent.setDataAndType(Uri.parse(imageDir), "image/*")
                getImageARL.launch(getImageIntent)
                true
            }
            R.id.chooserMi -> {
                val url : Uri = Uri.parse(amb.parametroTv.text.toString())
                val intent = Intent(Intent.ACTION_VIEW, url)

                val chooseAppIntent = Intent(Intent.ACTION_CHOOSER)
                chooseAppIntent.putExtra(Intent.EXTRA_TITLE, "Escolha um aplicativo")
                chooseAppIntent.putExtra(Intent.EXTRA_INTENT, intent)

                startActivity(chooseAppIntent)
                true
            }
            else -> false
        }
    }

    private fun callNumber(calling: Boolean) {
        val intent = Intent(if(calling) Intent.ACTION_CALL else Intent.ACTION_DIAL, Uri.parse("tel:${amb.parametroTv.text}"))
        startActivity(intent)
    }
}