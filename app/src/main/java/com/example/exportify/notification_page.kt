package com.example.exportify

import NotificationAdapter
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.exportify.databinding.ActivityNotificationPageBinding
import com.example.exportify.models.AdminNotificationModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class notification_page : AppCompatActivity() {

    private lateinit var binding: ActivityNotificationPageBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var uid:String
    private lateinit var databaseRef: DatabaseReference
    private var nList = ArrayList<AdminNotificationModel>()
    private lateinit var adapter: NotificationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backBtn.setOnClickListener {
            finish()
        }

        //initialize variables
        auth = FirebaseAuth.getInstance()
        uid = auth.currentUser?.uid.toString()
        databaseRef = FirebaseDatabase.getInstance().reference.child("admin_notifications")

        var recyclerView = binding.recyclerview

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this);

        retrieveData()

        adapter = NotificationAdapter(nList)
        recyclerView.adapter = adapter

        adapter.setOnNotificationClickListener(object : NotificationAdapter.OnNotificationClickListener {
            override fun onNotificationClick(position: Int) {
                intent = Intent(applicationContext, Admin_Notifi_Details::class.java).also {
                    it.putExtra("topic", nList[position].topic)
                    it.putExtra("type", nList[position].type)
                    it.putExtra("description", nList[position].description)
                    it.putExtra("id", nList[position].id)
                    startActivity(it)
                }
            }
        })

    }

    private fun retrieveData() {
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                nList.clear()
                for (snapshot in snapshot.children){
                    val notification = snapshot.getValue(AdminNotificationModel::class.java)!!
                    if (notification != null){
                        nList.add(notification)
                    }
                }
                adapter.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@notification_page, "Error", Toast.LENGTH_SHORT).show()
            }
        })
    }
}