package com.example.jobtracker

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.jobtracker.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val jobList = mutableListOf<JobApplication>()
    private lateinit var adapter: JobAdapter
    private lateinit var uid: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
            finish()
            return
        }
        uid = currentUser.uid

        adapter = JobAdapter(
            jobs = jobList,
            onEditClick = { job ->
                val intent = Intent(this, EditJobActivity::class.java)
                intent.putExtra("jobId", job.id)
                startActivity(intent)
            },
            onDeleteClick = { job ->
                confirmAndDeleteJob(job)
            }
        )

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        binding.fabAddJob.setOnClickListener {
            startActivity(Intent(this@MainActivity, AddJobActivity::class.java))
        }

        loadJobsFromFirestore(uid)
    }

    private fun loadJobsFromFirestore(uid: String) {
        val db = FirebaseFirestore.getInstance()
        db.collection("users").document(uid).collection("jobApplications")
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener

                jobList.clear()
                for (doc in snapshot.documents) {
                    val job = doc.toObject(JobApplication::class.java)
                    if (job != null) jobList.add(job)
                }
                adapter.notifyDataSetChanged()

                val isEmpty = jobList.isEmpty()
                binding.recyclerView.visibility = if (isEmpty) android.view.View.GONE else android.view.View.VISIBLE
                binding.emptyState.visibility = if (isEmpty) android.view.View.VISIBLE else android.view.View.GONE

                val appliedCount = jobList.size
                val interviewCount = jobList.count { it.status == "Interview" }
                val repliedCount = jobList.count {
                    it.status == "Interview" || it.status == "Offer" || it.status == "Rejected"
                }

                binding.txtAppliedCount.text = appliedCount.toString()
                binding.txtInterviewCount.text = interviewCount.toString()
                binding.txtReplyCount.text = repliedCount.toString()
            }
    }

    private fun confirmAndDeleteJob(job: JobApplication) {
        AlertDialog.Builder(this)
            .setTitle("Delete application?")
            .setMessage("This will permanently delete your application to ${job.company}. This can't be undone.")
            .setPositiveButton("Delete") { _, _ ->
                deleteJob(job.id)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteJob(jobId: String) {
        if (jobId.isEmpty()) return
        FirebaseFirestore.getInstance()
            .collection("users").document(uid)
            .collection("jobApplications").document(jobId)
            .delete()
    }
}