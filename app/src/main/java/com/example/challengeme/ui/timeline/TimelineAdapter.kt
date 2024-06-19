package com.example.challengeme.ui.timeline

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.challengeme.R
import java.text.SimpleDateFormat
import java.util.*

data class Post(val userName: String, val comment: String, val date: Date)

class TimelineAdapter(private val posts: List<Post>) : RecyclerView.Adapter<TimelineAdapter.PostViewHolder>() {

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userNameTextView: TextView = itemView.findViewById(R.id.userNameTextView)
        val commentTextView: TextView = itemView.findViewById(R.id.commentTextView)
        val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        return PostViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val currentPost = posts[position]
        holder.userNameTextView.text = currentPost.userName
        holder.commentTextView.text = currentPost.comment
        holder.dateTextView.text = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(currentPost.date)
    }

    override fun getItemCount() = posts.size
}
