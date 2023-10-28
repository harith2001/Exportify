package com.example.exportify

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.exportify.databinding.ActivityAdminProfileBinding
import com.example.exportify.models.AdminModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdminProfile : AppCompatActivity() {
    private lateinit var binding: ActivityAdminProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var uid: String
    private lateinit var databaseRef: DatabaseReference
    private lateinit var user: AdminModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //initialize variables
        auth = FirebaseAuth.getInstance()
        uid = auth.currentUser?.uid.toString()
        databaseRef = FirebaseDatabase.getInstance().reference.child("users")

        databaseRef.child(auth.currentUser!!.uid).addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                user = snapshot.getValue(AdminModel::class.java)!!

                binding.adname.text = user.fname
                binding.adLname.text = user.lname
                binding.ademail.text = user.email
                binding.adphone.text = user.contactNo
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@AdminProfile, "Failed to fetch user", Toast.LENGTH_SHORT).show()
            }
            })
        binding.btnBackad.setOnClickListener {
            intent = Intent(this, Admin_Dashboard::class.java)
            startActivity(intent)
        }

        binding.btnlogoutad.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            intent = Intent(this, Login::class.java)
            startActivity(intent)
        }

        binding.btndeletead.setOnClickListener {
            databaseRef.child(auth.currentUser!!.uid).removeValue()
            FirebaseAuth.getInstance().currentUser!!.delete()
            intent = Intent(this, Login::class.java)
            startActivity(intent)
        }

    }
}