package com.example.challengeme.ui.timeline

import Comment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.challengeme.R
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.Locale
import android.widget.ImageView
import java.util.TimeZone

class CommentAdapter(private val comments: List<Comment>) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_comment, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = comments[position]

        // ユーザー名を取得し、表示
        val userRef = FirebaseFirestore.getInstance().collection("users").document(comment.userId)
        userRef.get().addOnSuccessListener { userDoc ->
            val userName = userDoc.getString("user_name") ?: "Unknown"
            holder.commentUserName.text = userName

            // ユーザーアイコンをPicassoで丸く表示
            val userIconUrl = userDoc.getString("user_icon")
            if (!userIconUrl.isNullOrEmpty()) {
                Picasso.get().load(userIconUrl).transform(TimelineAdapter.CircleTransform()).into(holder.commentUserIcon)
            } else {
                holder.commentUserIcon.setImageResource(R.drawable.default_profile)
            }
        }

        // コメントテキストを表示
        holder.commentText.text = comment.commentText

        // コメントの時間を表示 (commentedAt)
        val dateFormat = SimpleDateFormat("yyyy年MM月dd日 HH:mm", Locale.getDefault())
        dateFormat.timeZone = TimeZone.getTimeZone("Asia/Tokyo")  // タイムゾーンをUTC+9に設定
        val dateStr = comment.commentedAt?.let { dateFormat.format(it.toDate()) } ?: "不明"
        holder.commentTime.text = dateStr

    }

    override fun getItemCount(): Int = comments.size

    class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val commentText: TextView = itemView.findViewById(R.id.commentText) // コメントの内容
        val commentUserName: TextView = itemView.findViewById(R.id.commentUserName) // ユーザー名 (太字に設定されている)
        val commentUserIcon: ImageView = itemView.findViewById(R.id.commentUserIcon) // ユーザーのアイコン
        val commentTime: TextView = itemView.findViewById(R.id.commentTime) // コメントの日時
    }
}
