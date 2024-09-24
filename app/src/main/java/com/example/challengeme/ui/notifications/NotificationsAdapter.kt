package com.example.challengeme.ui.notifications

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.challengeme.databinding.ItemNotificationBinding
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore

data class NotificationItem(
    val type: String,
    val userId: String,
    val timelineId: String,
    val timestamp: Timestamp?
)

class NotificationsAdapter(private var notifications: MutableList<NotificationItem>) :
    RecyclerView.Adapter<NotificationsAdapter.NotificationViewHolder>() {

    class NotificationViewHolder(val binding: ItemNotificationBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val binding = ItemNotificationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NotificationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val notification = notifications[position]

        FirebaseFirestore.getInstance().collection("users").document(notification.userId).get()
            .addOnSuccessListener { document ->
                val userName = document.getString("user_name") ?: "Unknown"
                if (notification.type == "like") {
                    holder.binding.notificationText.text = "$userName があなたの投稿にリアクションしました"
                } else if (notification.type == "comment") {
                    holder.binding.notificationText.text = "$userName があなたの投稿にコメントしました"
                }
            }
    }

    override fun getItemCount(): Int = notifications.size

    fun updateData(newNotifications: List<NotificationItem>) {
        notifications.clear()
        notifications.addAll(newNotifications)
        notifyDataSetChanged()
    }
}
