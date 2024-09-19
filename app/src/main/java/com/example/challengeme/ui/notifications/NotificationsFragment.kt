package com.example.challengeme.ui.notifications

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.challengeme.databinding.FragmentNotificationsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!
    private lateinit var notificationsAdapter: NotificationsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.notificationsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        notificationsAdapter = NotificationsAdapter(mutableListOf())
        binding.notificationsRecyclerView.adapter = notificationsAdapter

        fetchNotifications()
    }

    private fun fetchNotifications() {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        if (currentUserId != null) {
            FirebaseFirestore.getInstance().collection("notifications")
                .whereEqualTo("postOwnerId", currentUserId)
                .orderBy("timestamp")
                .get()
                .addOnSuccessListener { result ->
                    val notifications = mutableListOf<NotificationItem>()
                    for (document in result) {
                        val type = document.getString("type") ?: ""
                        val userId = if (type == "like") {
                            document.getString("likedByUserId") ?: ""
                        } else {
                            document.getString("commentedByUserId") ?: ""
                        }
                        notifications.add(
                            NotificationItem(
                                type = type,
                                userId = userId,
                                timelineId = document.getString("timelineId") ?: "",
                                timestamp = document.getTimestamp("timestamp")
                            )
                        )
                    }
                    notificationsAdapter.updateData(notifications)
                }
                .addOnFailureListener { e ->
                    Log.e("NotificationsFragment", "Error fetching notifications", e)
                }
        } else {
            Log.d("NotificationsFragment", "User is not logged in.")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
