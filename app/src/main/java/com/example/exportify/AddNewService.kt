package com.example.exportify

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.exportify.databinding.ActivityAddNewServiceBinding
import com.example.exportify.models.ServiceGig
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import android.app.Activity

class AddNewService : AppCompatActivity() {
    private lateinit var binding: ActivityAddNewServiceBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var databaseRef: DatabaseReference
    private lateinit var uid: String
    private lateinit var storageReference: StorageReference
    private val IMAGE_PICK_REQUEST = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNewServiceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backBtn.setOnClickListener {
            intent = Intent(this, Exporter_dashboard::class.java)
            startActivity(intent)
        }
        binding.addImgBtn.setOnClickListener {
         addimage()
        }



        //initialize variables
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        uid = auth.currentUser?.uid.toString()
        databaseRef = FirebaseDatabase.getInstance().reference.child("service_gigs")
        storageReference = FirebaseStorage.getInstance().reference

        binding.btnInsert.setOnClickListener {
            saveServiceGigData("")
        }
    }

    private fun saveServiceGigData(imageUrl: String ) {
        val topic = binding.edServiceTopic.text.toString()
        val type = binding.edServiceType.text.toString()
        val des = binding.edDescription.text.toString()
        val noOfUnits = binding.edUnits.text.toString()
        val price = binding.edPrice.text.toString()


        //checking if the input fields are empty
        if (topic.isEmpty() || type.isEmpty() || des.isEmpty() || noOfUnits.isEmpty() || price.isEmpty()) {
            if (topic.isEmpty()) {
                binding.edServiceTopic.error = "Enter sevice topic"
            }
            if (type.isEmpty()) {
                binding.edServiceType.error = "Enter service type"
            }
            if (des.isEmpty()) {
                binding.edDescription.error = "Enter service description"
            }
            if (noOfUnits.isEmpty()) {
                binding.edUnits.error = "Enter number of units unit available"
            }
            if (price.isEmpty()) {
                binding.edPrice.error = "Enter price"
            }
            Toast.makeText(this, "Enter valid details", Toast.LENGTH_SHORT).show()
        }

        //validate noOfUnits
        else if (noOfUnits.length == 0) {
            binding.edUnits.error = "Enter a valid phone number"
        } else {
            var id = databaseRef.push().key!!
            val servicegig: ServiceGig =
                ServiceGig(topic, type, des, noOfUnits, price, id, uid, imageUrl)
            databaseRef.child(id).setValue(servicegig).addOnCompleteListener {
                if (it.isSuccessful) {
                    //redirect user to login activity
                    val intent = Intent(this, Exporter_dashboard::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(
                        this,
                        "Something went wrong, try again",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICK_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val imageUri = data.data
            if (imageUri != null) {
                uploadImage(imageUri)
            }
        }
    }
    private fun uploadImage(imageUri: Uri) {
        val imageRef = storageReference.child("images/${System.currentTimeMillis()}.jpg")
        val uploadTask = imageRef.putFile(imageUri)

        uploadTask.addOnSuccessListener {
            // Image upload successful, get the download URL
            imageRef.downloadUrl.addOnSuccessListener { uri ->
                val imageUrl = uri.toString()
                // Save the imageUrl in the service gigs table
                saveServiceGigData(imageUrl)
            }
        }.addOnFailureListener {
            // Image upload failed
            Toast.makeText(this, "Image upload failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addimage() {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent, IMAGE_PICK_REQUEST)
        }
    }
