package ademkocamaz.yapcam.adapter

import ademkocamaz.yapcam.R
import ademkocamaz.yapcam.model.Yapcam
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class RecyclerAdapter() : RecyclerView.Adapter<RecyclerAdapter.RecyclerHolder>() {

    class RecyclerHolder(val view: View) : RecyclerView.ViewHolder(view) {

    }

    private val auth=Firebase.auth
    private val firestore=Firebase.firestore

    private val diffUtil = object : DiffUtil.ItemCallback<Yapcam>() {
        override fun areItemsTheSame(oldItem: Yapcam, newItem: Yapcam): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Yapcam, newItem: Yapcam): Boolean {
            return oldItem == newItem
        }

    }

    private val recyclerListDiffer = AsyncListDiffer(this, diffUtil)

    var yapcams: List<Yapcam>
        get() = recyclerListDiffer.currentList
        set(value) = recyclerListDiffer.submitList(value)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_row, parent, false)
        return RecyclerHolder(view)
    }



    override fun onBindViewHolder(holder: RecyclerHolder, position: Int) {
        val checkBox = holder.view.findViewById<CheckBox>(R.id.recycler_row_checkBox)
        checkBox.text = "${yapcams.get(position).text}"
        checkBox.isChecked = yapcams.get(position).checked

        checkBox.setOnClickListener {
            firestore.collection(auth.currentUser!!.email.toString())
                .document(yapcams.get(position).id)
                .update("checked",checkBox.isChecked)
        }


    }

    override fun getItemCount(): Int {
        return yapcams.size
    }
}