package com.example.exportify

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.exportify.databinding.ActivityAdminSignupBinding
import com.example.exportify.models.AdminModel
import com.example.exportify.models.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class Admin_Signup : AppCompatActivity() {
    private lateinit var binding: ActivityAdminSignupBinding
    private var validityCount = 0
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminSignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Initializing auth and database variables
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        binding.btnCancel.setOnClickListener{
            intent = Intent(applicationContext, User_direction::class.java)
            startActivity(intent)
        }
        binding.btnRegister12.setOnClickListener{
                createUser()
        }

    }

    private fun createUser() {
        val fname = binding.etFName1.text.toString()
        val lname = binding.etLName1.text.toString()
        val pwd = binding.etpass1.text.toString()
        val email = binding.etEmail1.text.toString()
        val contactNo = binding.etContactNum1.text.toString()

        auth.createUserWithEmailAndPassword(email, pwd).addOnCompleteListener {

            if (it.isSuccessful) {
                //store user details in the database
                val databaseRef =
                    database.reference.child("users").child(auth.currentUser!!.uid)
                val user= AdminModel(fname,lname,pwd,email,contactNo,"admin",auth.currentUser!!.uid)
                databaseRef.setValue(user).addOnCompleteListener {
                    if (it.isSuccessful) {
                        //redirect user to login activity
                        val intent = Intent(this, Login::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(
                            this,
                            "Something went wrong, try again",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            } else {
                Toast.makeText(this, "Something went wrong, try again", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
}