package ademkocamaz.yapcam.ui

import ademkocamaz.yapcam.R
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = Firebase.auth
        auth.signInAnonymously().addOnSuccessListener {
            val intent = Intent(this, YapcamActivity::class.java)
            startActivity(intent)
            finish()
        }.addOnFailureListener {
            Toast.makeText(this, it.localizedMessage, Toast.LENGTH_LONG).show()
        }
        /*val currentUser = auth.currentUser
        if (currentUser != null) {
            val intent = Intent(this, YapcamActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }*/
    }
}