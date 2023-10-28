package com.example.exportify

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.exportify.databinding.ActivityAdminDashboardBinding


class Admin_Dashboard : AppCompatActivity() {
    private lateinit var binding: ActivityAdminDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnDashboardProfile.setOnClickListener {
            intent = Intent(this, Admin_Notifi_Dashboard::class.java)
            startActivity(intent)
        }
        binding.btnDashboardNotifi.setOnClickListener {
            intent = Intent(this, AdminProfile::class.java)
            startActivity(intent)
        }
    }
}