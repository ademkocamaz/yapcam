package ademkocamaz.yapcam.ui

import ademkocamaz.yapcam.R
import ademkocamaz.yapcam.adapter.RecyclerAdapter
import ademkocamaz.yapcam.databinding.ActivityYapcamBinding
import ademkocamaz.yapcam.model.Yapcam
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class YapcamActivity : AppCompatActivity() {
    private lateinit var binding: ActivityYapcamBinding

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private lateinit var adapter: RecyclerAdapter
    private var yapcams = arrayListOf<Yapcam>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityYapcamBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.adView.loadAd(AdRequest.Builder().build())

        InterstitialAd.load(this,"ca-app-pub-5764318432941968/8886634175", AdRequest.Builder().build(), object : InterstitialAdLoadCallback() {
            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                interstitialAd.show(this@YapcamActivity)
            }
        })

        firestore = Firebase.firestore
        auth = Firebase.auth
        val useruuid = auth.currentUser?.uid.toString()

        val layoutManager = LinearLayoutManager(this)
        binding.yapcamRecyclerView.layoutManager = layoutManager
        adapter = RecyclerAdapter()
        binding.yapcamRecyclerView.adapter = adapter

        binding.yapcamButtonEkle.setOnClickListener {

            if (binding.yapcamEditTextYapcam.text.toString() != "") {

                auth.currentUser?.let { user ->
                    val text = binding.yapcamEditTextYapcam.text.toString()
                    binding.yapcamEditTextYapcam.setText("")

                    val date = FieldValue.serverTimestamp()

                    val dataMap = HashMap<String, Any>()
                    dataMap.put("text", text)
                    dataMap.put("checked", false)
                    dataMap.put("date", date)

                    firestore.collection(useruuid).add(dataMap)
                        .addOnSuccessListener {
                            binding.yapcamEditTextYapcam.setText("")
                        }

                        .addOnFailureListener { exception ->
                            binding.yapcamEditTextYapcam.setText("")
                            Toast.makeText(
                                this,
                                exception.localizedMessage,
                                Toast.LENGTH_LONG
                            ).show()
                        }
                }
            }
        }

        firestore.collection(useruuid)
            .orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { snap, exception ->
                if (exception != null) {
                    Toast.makeText(
                        this,
                        exception.localizedMessage,
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    if (snap != null) {
                        if (snap.isEmpty) {
                            yapcams.clear()
                            adapter.notifyDataSetChanged()
                            Toast.makeText(
                                this,
                                "Liste (Boş)",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            val documents = snap.documents
                            yapcams.clear()
                            for (document in documents) {

                                val text = document.get("text") as String
                                val checked = document.get("checked") as Boolean
                                val yapcam = Yapcam(document.id, text, checked)
                                yapcams.add(yapcam)
                                adapter.yapcams = yapcams
                            }
                            adapter.notifyDataSetChanged()


                        }
                    }
                }
            }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {


        if (item.itemId == R.id.main_menu_temizle) {

            for (yapcam in yapcams) {
                firestore
                    .collection(auth.currentUser!!.uid.toString())
                    .document(yapcam.id)
                    .delete()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}