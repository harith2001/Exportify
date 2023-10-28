package com.example.exportify

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.exportify.databinding.ActivityAdminNewNotifiBinding
import com.example.exportify.models.AdminNotificationModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class Admin_New_Notifi : AppCompatActivity() {
    private lateinit var binding: ActivityAdminNewNotifiBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseRef: DatabaseReference
    private lateinit var storageRef:StorageReference
    private val PICK_PDF_REQUEST = 1
    private lateinit var uid:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminNewNotifiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        uid = auth.currentUser?.uid.toString()
        databaseRef = FirebaseDatabase.getInstance().reference
            .child("admin_notifications")
        storageRef = FirebaseStorage.getInstance().reference

        binding.backBtn.setOnClickListener {
            intent = Intent(this, Admin_Notifi_Dashboard::class.java)
            startActivity(intent)
        }
        binding.addImgBtn.setOnClickListener {
            val intent = Intent()
            intent.type = "application/pdf"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select PDF"), PICK_PDF_REQUEST)
        }

        binding.btnInsert.setOnClickListener {
            saveNotificationData("")
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_PDF_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val pdfUri: Uri = data.data!!
            uploadPdfFile(pdfUri)
        }
    }

    private fun uploadPdfFile(pdfUri: Uri) {
        val storageReference = storageRef.child("pdfs").child("${System.currentTimeMillis()}.pdf")

        storageReference.putFile(pdfUri)
            .addOnSuccessListener { taskSnapshot ->
                // File uploaded successfully, now get the download URL
                taskSnapshot.storage.downloadUrl.addOnSuccessListener { uri ->
                    // Retrieve the download URL
                    val downloadUrl = uri.toString()

                    // After getting the download URL, save the notification data including the URL in the database
                    saveNotificationData(downloadUrl)
                }
            }
            .addOnFailureListener {
                // Handle error
                Toast.makeText(applicationContext, "Failed to upload PDF", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveNotificationData(downloadUrl: String) {

        var topic = binding.etNotifiTopic.text.toString()
        var type = binding.etNotifiType.text.toString()
        var description = binding.etPriceRange.text.toString()

        if (topic.isEmpty() || type.isEmpty() || description.isEmpty()) {
            if (topic.isEmpty()) {
                binding.etNotifiTopic.error = "Enter Topic"
            }
            if (type.isEmpty()) {
                binding.etNotifiType.error = "Enter Type"
            }
            if (description.isEmpty()) {
                binding.etPriceRange.error = "Enter Description"
            }
        } else {
            var id = databaseRef.push().key!!
            val notifi = AdminNotificationModel(topic, type, description, id, uid, downloadUrl)
            databaseRef.child(id).setValue(notifi).addOnCompleteListener {
                if (it.isSuccessful) {
                    intent = Intent(applicationContext, Admin_Notifi_Dashboard::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(applicationContext, "Error", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}