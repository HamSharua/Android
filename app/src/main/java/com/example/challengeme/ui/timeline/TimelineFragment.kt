package com.example.challengeme.ui.timeline

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.challengeme.databinding.FragmentTimelineBinding
import com.google.firebase.firestore.FirebaseFirestore

class TimelineFragment : Fragment() {

    private var _binding: FragmentTimelineBinding? = null
    private val binding get() = _binding!!
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTimelineBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // RecyclerView のレイアウトマネージャーを設定
        binding.timelineRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        fetchTimelineData()
    }

    private fun fetchTimelineData() {
        firestore.collection("timeline")
            .get()
            .addOnSuccessListener { result ->
                val timelineItems = mutableListOf<TimelineItem>()
                for (document in result) {
                    val userId = document.getString("user_id") ?: ""
                    val comment = document.getString("comment") ?: ""
                    val imageUrl = document.getString("image") ?: ""

                    // timeline の user_id に対応する users コレクションの user_icon と user_name を取得
                    firestore.collection("users").document(userId).get()
                        .addOnSuccessListener { userDoc ->
                            val userName = userDoc.getString("user_name") ?: ""
                            val userIcon = userDoc.getString("user_icon") ?: ""

                            val timelineItem = TimelineItem(
                                userId = userId,
                                userName = userName,
                                userIcon = userIcon,
                                comment = comment,
                                imageUrl = imageUrl
                            )
                            timelineItems.add(timelineItem)

                            // RecyclerView のアダプタをセット
                            binding.timelineRecyclerView.adapter = TimelineAdapter(timelineItems)
                        }
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
