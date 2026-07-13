package com.example.jobtracker

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.jobtracker.databinding.ActivityEditJobBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar

class EditJobActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditJobBinding
    private var jobId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityEditJobBinding.inflate(layoutInflater)
        setContentView(binding.root)

        jobId = intent.getStringExtra("jobId") ?: ""
        if (jobId.isEmpty()) {
            Toast.makeText(this, "Invalid job entry", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val statusOptions = listOf("Applied", "Interview", "Rejected", "Offer")
        val statusAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, statusOptions)
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

        binding.btnBack.setOnClickListener { finish() }

        loadExistingJob()

        binding.addJobApplication.text = "Update Application"
        binding.addJobApplication.setOnClickListener {
            updateJob()
        }
    }

    private fun loadExistingJob() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        FirebaseFirestore.getInstance()
            .collection("users").document(uid)
            .collection("jobApplications").document(jobId)
            .get()
            .addOnSuccessListener { doc ->
                val job = doc.toObject(JobApplication::class.java) ?: return@addOnSuccessListener
                binding.companyInput.setText(job.company)
                binding.titleInput.setText(job.title)
                binding.salaryInput.setText(job.salary)
                binding.autoCompleteTextView.setText(job.status, false)
                binding.dateEditText.setText(job.dateApplied)
                binding.checkBoxInternship.isChecked = job.isInternship
            }
    }

    private fun updateJob() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid == null) {
            Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show()
            return
        }

        val company = binding.companyInput.text.toString().trim()
        val title = binding.titleInput.text.toString().trim()

        if (company.isEmpty() || title.isEmpty()) {
            Toast.makeText(this, "Enter company name and job title", Toast.LENGTH_SHORT).show()
            return
        }

        val updatedJob = JobApplication(
            company = company,
            title = title,
            status = binding.autoCompleteTextView.text.toString().trim(),
            isInternship = binding.checkBoxInternship.isChecked,
            salary = binding.salaryInput.text.toString().trim(),
            dateApplied = binding.dateEditText.text.toString().trim()
        )

        FirebaseFirestore.getInstance()
            .collection("users").document(uid)
            .collection("jobApplications").document(jobId)
            .set(updatedJob)
            .addOnSuccessListener {
                Toast.makeText(this, "Application updated", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show()
            }
    }
}