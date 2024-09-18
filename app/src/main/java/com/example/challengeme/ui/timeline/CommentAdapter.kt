package com.example.challengeme.ui.timeline

import Comment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.challengeme.R
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class CommentAdapter(private val comments: List<Comment>) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_comment, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = comments[position]

        // コメントテキストをセット
        holder.commentText.text = comment.commentText

        // Firestoreからユーザーのアイコンと名前を取得してセット
        val userRef = FirebaseFirestore.getInstance().collection("users").document(comment.userId)
        userRef.get().addOnSuccessListener { userDoc ->
            val userName = userDoc.getString("user_name") ?: "Unknown"
            val userIconUrl = userDoc.getString("user_icon")

            // ユーザー名をセット
            holder.commentUserName.text = userName

            // ユーザーアイコンを丸く表示
            if (!userIconUrl.isNullOrEmpty()) {
                Picasso.get().load(userIconUrl)
                    .transform(TimelineAdapter.CircleTransform())  // 丸く変換
                    .into(holder.commentUserIcon)
            } else {
                holder.commentUserIcon.setImageResource(R.drawable.default_profile)
            }
        }
    }


    override fun getItemCount(): Int = comments.size

    class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val commentText: TextView = itemView.findViewById(R.id.commentText)
        val commentUserName: TextView = itemView.findViewById(R.id.commentUserName)
        val commentUserIcon: ImageView = itemView.findViewById(R.id.commentUserIcon)
    }
}

