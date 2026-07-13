package com.example.jobtracker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.jobtracker.databinding.ItemJobBinding

class JobAdapter(
    private val jobs: List<JobApplication>,
    private val onEditClick: (JobApplication) -> Unit,
    private val onDeleteClick: (JobApplication) -> Unit
) : RecyclerView.Adapter<JobAdapter.JobViewHolder>() {

    inner class JobViewHolder(val binding: ItemJobBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobViewHolder {
        val binding = ItemJobBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return JobViewHolder(binding)
    }

    override fun onBindViewHolder(holder: JobViewHolder, position: Int) {
        val job = jobs[position]
        holder.binding.companyName.text = job.company
        holder.binding.jobTitle.text = job.title
        holder.binding.statusTag.text = job.status
        holder.binding.dateApplied.text = job.dateApplied
        holder.binding.statusTag.setBackgroundColor(job.statusColor())
        holder.binding.statusTag.setTextColor(job.statusTextColor())

        holder.binding.btnEdit.setOnClickListener {
            onEditClick(job)
        }

        holder.binding.btnDelete.setOnClickListener {
            onDeleteClick(job)
        }
    }

    override fun getItemCount(): Int {
        return jobs.size
    }
}