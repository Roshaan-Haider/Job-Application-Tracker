package com.example.jobtracker

import android.graphics.Color
import com.google.firebase.firestore.DocumentId

data class JobApplication(
    val company: String = "",
    val title: String = "",
    val status: String = "Applied",
    val isInternship: Boolean = false,
    val salary: String = "",
    val dateApplied: String = "",
    @DocumentId val id: String = ""
)

fun JobApplication.statusColor(): Int {
    return when (status) {
        "Rejected" -> Color.parseColor("#FF5252")
        "Interview" -> Color.parseColor("#66BB6A")
        "Offer" -> Color.parseColor("#1B5E20")
        else -> Color.parseColor("#90A4AE")
    }
}

fun JobApplication.statusTextColor(): Int {
    return when (status) {
        "Rejected" -> Color.WHITE
        "Applied" -> Color.BLACK
        "Interview" -> Color.BLACK
        "Offer" -> Color.WHITE
        else -> Color.WHITE
    }
}