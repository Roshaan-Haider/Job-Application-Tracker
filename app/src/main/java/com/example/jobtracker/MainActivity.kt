package com.example.jobtracker

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import com.example.jobtracker.databinding.ActivityLoginBinding
import com.example.jobtracker.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import androidx.recyclerview.widget.LinearLayoutManager

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = JobAdapter(fakeJobs)

       

    }
    val fakeJobs = listOf(
        JobApplication("TechCorp", "Software Engineer", "Applied", true),
        JobApplication("InnovateLLC", "Product Manager", "Interview", false),
        JobApplication("DataWorks", "Data Scientist", "Rejected", false),
        JobApplication("BuildRight", "Backend Developer", "Offer", true)
    )

}