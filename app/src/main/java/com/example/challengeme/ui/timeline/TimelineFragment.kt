package com.example.challengeme.ui.timeline

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.challengeme.databinding.FragmentTimelineBinding
import com.example.challengeme.ui.timeline.model.TimelineItem
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class TimelineFragment : Fragment() {

    private var _binding: FragmentTimelineBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: FirebaseFirestore
    private lateinit var adapter: TimelineAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTimelineBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = Firebase.firestore
        setupRecyclerView()
        fetchTimelineData()
    }

    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = TimelineAdapter(emptyList())
        binding.recyclerView.adapter = adapter
    }

    private fun fetchTimelineData() {
        db.collection("timeline")
            .get()
            .addOnSuccessListener { documents ->
                val timelineItems = mutableListOf<TimelineItem>()
                for (document in documents) {
                    try {
                        val item = document.toObject(TimelineItem::class.java)
                        timelineItems.add(item)
                    } catch (e: Exception) {
                        Log.e("TimelineFragment", "Error converting document to TimelineItem", e)
                    }
                }
                adapter = TimelineAdapter(timelineItems)
                binding.recyclerView.adapter = adapter
            }
            .addOnFailureListener { exception ->
                Log.e("TimelineFragment", "Error fetching timeline data", exception)
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
