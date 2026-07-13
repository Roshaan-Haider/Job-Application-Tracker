package com.example.jobtracker


import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.jobtracker.databinding.ActivityAddJobBinding
import java.util.Calendar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.text.clear

class AddJobActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddJobBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAddJobBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val statusOptions = listOf("Applied", "Interview", "Rejected", "Offer")
        val statusAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, statusOptions)
        val calendar = Calendar.getInstance()
        val todayString = "${calendar.get(Calendar.DAY_OF_MONTH)}/${calendar.get(Calendar.MONTH) + 1}/${calendar.get(Calendar.YEAR)}"
        binding.dateEditText.setText(todayString)


        binding.autoCompleteTextView.setAdapter(statusAdapter)
        binding.dateEditText.setOnClickListener {

            val calendar = Calendar.getInstance()
            DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    binding.dateEditText.setText("$dayOfMonth/${month + 1}/$year")
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        binding.addJobApplication.setOnClickListener {
            binding.addJobApplication.isEnabled = false
            val company = binding.companyInput.text.toString().trim()
            val title = binding.titleInput.text.toString().trim()
            val salary = binding.salaryInput.text.toString().trim()
            val status = binding.autoCompleteTextView.text.toString().trim()
            val dateApplied = binding.dateEditText.text.toString().trim()
            val isInternship = binding.checkBoxInternship.isChecked

            if (company.isEmpty() || title.isEmpty()) {
                Toast.makeText(this, "Enter company name and Job title", Toast.LENGTH_SHORT).show()
                binding.addJobApplication.isEnabled = true
                return@setOnClickListener
            }

            val uid = FirebaseAuth.getInstance().currentUser?.uid
            if (uid == null) {
                Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show()
                binding.addJobApplication.isEnabled = true
                return@setOnClickListener
            }

            val newJob = JobApplication(
                company = company,
                title = title,
                status = status,
                isInternship = isInternship,
                salary = salary,
                dateApplied = dateApplied
            )

            val db = FirebaseFirestore.getInstance()
            db.collection("users").document(uid).collection("jobApplications")
                .add(newJob)
                .addOnSuccessListener {
                    Toast.makeText(this, "Jon application Added", Toast.LENGTH_SHORT).show()
                    val calendar = Calendar.getInstance()
                    val todayString = "${calendar.get(Calendar.DAY_OF_MONTH)}/${calendar.get(Calendar.MONTH) + 1}/${calendar.get(Calendar.YEAR)}"
                    binding.dateEditText.setText(todayString)
                    binding.titleInput.text?.clear()
                    binding.salaryInput.text?.clear()
                    binding.companyInput.text?.clear()


                    binding.checkBoxInternship.isChecked=false
                    binding.autoCompleteTextView.setText("")
                    binding.addJobApplication.isEnabled = true

                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "unable to add job application", Toast.LENGTH_SHORT).show()
                    binding.addJobApplication.isEnabled = true

                }
        }

        binding.btnBack.setOnClickListener {
            val intent = Intent(this@AddJobActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        val card = findViewById<LinearLayout>(R.id.formCard)
        card.alpha = 0f
        card.translationY = 40f
        card.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(350)
            .setInterpolator(android.view.animation.DecelerateInterpolator())
            .start()
    }






}