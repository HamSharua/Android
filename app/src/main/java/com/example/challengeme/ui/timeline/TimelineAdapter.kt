package com.example.challengeme.ui.timeline

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.challengeme.R
import com.example.challengeme.databinding.ItemTimelineBinding
import com.example.challengeme.ui.timeline.model.TimelineItem

class TimelineAdapter(private val timelineItems: List<TimelineItem>) :
    RecyclerView.Adapter<TimelineAdapter.TimelineViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimelineViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemTimelineBinding.inflate(inflater, parent, false)
        return TimelineViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TimelineViewHolder, position: Int) {
        val timelineItem = timelineItems[position]
        holder.bind(timelineItem)
    }

    override fun getItemCount() = timelineItems.size

    class TimelineViewHolder(private val binding: ItemTimelineBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(timelineItem: TimelineItem) {
            binding.textViewComment.text = timelineItem.comment
            Glide.with(binding.imageView.context)
                .load(timelineItem.image)
                .placeholder(R.drawable.placeholder)
                .into(binding.imageView)
        }
    }
}
