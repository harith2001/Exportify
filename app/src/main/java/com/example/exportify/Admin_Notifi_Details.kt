package com.example.exportify

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.exportify.databinding.ActivityAdminNotifiDetailsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class Admin_Notifi_Details : AppCompatActivity() {
    private lateinit var binding: ActivityAdminNotifiDetailsBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseRef: DatabaseReference
    private lateinit var uid:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminNotifiDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.imageView3.setOnClickListener {
            intent = Intent(this, Admin_Notifi_Dashboard::class.java)
            startActivity(intent)
        }

        //fetch the data from the intent
        val topic = intent.getStringExtra("topic").toString()
        val type = intent.getStringExtra("type").toString()
        val description = intent.getStringExtra("description").toString()
        val reqid = intent.getStringExtra("id").toString()

        binding.ednotifiTopic.setText(topic)
        binding.edNotifiType.setText(type)
        binding.edDescription.setText(description)

        //initialize variables
        auth = FirebaseAuth.getInstance()
        uid = auth.currentUser?.uid.toString()
        databaseRef = FirebaseDatabase.getInstance().getReference("admin_notifications")

        binding.btnUpdate.setOnClickListener {
            val map = HashMap<String,Any>()

            var edTopic = binding.ednotifiTopic.text.toString()
            var edType = binding.edNotifiType.text.toString()
            var edDescription = binding.edDescription.text.toString()

            map["topic"] = edTopic
            map["type"] = edType
            map["description"] = edDescription

            databaseRef.child(reqid).updateChildren(map).addOnSuccessListener {
                Toast.makeText(this, "Updated Successfully", Toast.LENGTH_SHORT).show()
                intent = Intent(this, Admin_Notifi_Dashboard::class.java)
                startActivity(intent)
            }.addOnFailureListener {
                Toast.makeText(this, "Failed to Update", Toast.LENGTH_SHORT).show()
            }

        }

        binding.btnDlt.setOnClickListener {
            databaseRef.child(reqid).removeValue().addOnSuccessListener {
                Toast.makeText(this, "Deleted Successfully", Toast.LENGTH_SHORT).show()
                intent = Intent(this, Admin_Notifi_Dashboard::class.java)
                startActivity(intent)
            }.addOnFailureListener {
                Toast.makeText(this, "Failed to Delete", Toast.LENGTH_SHORT).show()
            }
        }

    }
}