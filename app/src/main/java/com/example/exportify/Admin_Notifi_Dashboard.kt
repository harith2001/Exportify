package com.example.exportify

import NotificationAdapter
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.exportify.databinding.ActivityAdminNewNotifiBinding
import com.example.exportify.databinding.ActivityAdminNotifiDashboardBinding
import com.example.exportify.models.AdminNotificationModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

class Admin_Notifi_Dashboard : AppCompatActivity() {
    private lateinit var binding: ActivityAdminNotifiDashboardBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseRef:DatabaseReference
    private var nList = ArrayList<AdminNotificationModel>()
    private lateinit var adapter: NotificationAdapter
    private lateinit var uid:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminNotifiDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivProfile.setOnClickListener {
            intent = Intent(this, AdminProfile::class.java)
            startActivity(intent)
        }

        binding.btnAddRequest.setOnClickListener {
             intent = Intent(this, Admin_New_Notifi::class.java)
            startActivity(intent)
        }
        binding.backBtn.setOnClickListener {
             intent = Intent(this, Admin_Dashboard::class.java)
            startActivity(intent)
        }

        //initialize variables
        auth = FirebaseAuth.getInstance()
        uid = auth.currentUser?.uid.toString()
        databaseRef = com.google.firebase.database.FirebaseDatabase.getInstance().getReference("admin_notifications")


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
                        it.putExtra("pdfUrl", nList[position].pdfUrl)
                        startActivity(it)
                    }
            }
        })

    }

    private fun retrieveData(){
        databaseRef.addValueEventListener(object :ValueEventListener{
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
                Toast.makeText(this@Admin_Notifi_Dashboard, "Error", Toast.LENGTH_SHORT).show()
            }
        })
    }

}