package com.example.challengeme.ui.timeline
import TimelineItem
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.challengeme.R
import com.example.challengeme.databinding.ItemTimelineBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class TimelineAdapter(private val timelineItems: List<TimelineItem>) :
    RecyclerView.Adapter<TimelineAdapter.TimelineViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimelineViewHolder {
        val binding = ItemTimelineBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TimelineViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TimelineViewHolder, position: Int) {
        val timelineItem = timelineItems[position]

        // ユーザー名、アイコン、コメント、画像をセット
        holder.binding.userName.text = timelineItem.userName
        Picasso.get().load(timelineItem.userIcon).into(holder.binding.userIcon)
        holder.binding.commentText.text = timelineItem.comment
        Picasso.get().load(timelineItem.imageUrl).into(holder.binding.postImage)

        // いいねカウントを表示
        holder.binding.likeCountTextView.text = timelineItem.likeCount.toString()

        // いいね状態の確認
        checkIfLiked(holder, timelineItem)

        // いいねボタンのクリックリスナーを設定
        holder.binding.likeButton.setOnClickListener {
            // いいね状態を反転
            toggleLike(holder, timelineItem)
        }
    }

    override fun getItemCount(): Int = timelineItems.size

    // いいね状態を確認してアイコンを更新する
    private fun checkIfLiked(holder: TimelineViewHolder, timelineItem: TimelineItem) {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        val likesRef = FirebaseFirestore.getInstance().collection("timeline")
            .document(timelineItem.timelineId).collection("likes").document(currentUserId!!)

        likesRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                // いいね済みの場合
                timelineItem.isLiked = true
                holder.binding.likeButton.setImageResource(R.drawable.ic_heart_outline_after)
            } else {
                // いいねしていない場合
                timelineItem.isLiked = false
                holder.binding.likeButton.setImageResource(R.drawable.ic_heart_outline)
            }
        }
    }

    // いいねを切り替えるメソッド
    private fun toggleLike(holder: TimelineViewHolder, timelineItem: TimelineItem) {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        val postRef = FirebaseFirestore.getInstance().collection("timeline").document(timelineItem.timelineId)
        val likesRef = postRef.collection("likes").document(currentUserId!!)

        if (timelineItem.isLiked) {
            // いいねを削除
            likesRef.delete().addOnSuccessListener {
                postRef.update("likeCount", FieldValue.increment(-1))
                holder.binding.likeButton.setImageResource(R.drawable.ic_heart_outline)
                timelineItem.isLiked = false
                timelineItem.likeCount--
                holder.binding.likeCountTextView.text = timelineItem.likeCount.toString()
            }
        } else {
            // いいねを追加
            val likeData = hashMapOf("likedAt" to com.google.firebase.Timestamp.now())
            likesRef.set(likeData).addOnSuccessListener {
                postRef.update("likeCount", FieldValue.increment(1))
                holder.binding.likeButton.setImageResource(R.drawable.ic_heart_outline_after)
                timelineItem.isLiked = true
                timelineItem.likeCount++
                holder.binding.likeCountTextView.text = timelineItem.likeCount.toString()
            }
        }
    }

    inner class TimelineViewHolder(val binding: ItemTimelineBinding) : RecyclerView.ViewHolder(binding.root)
}
