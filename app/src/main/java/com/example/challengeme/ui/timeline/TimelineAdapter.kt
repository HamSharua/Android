package com.example.challengeme.ui.timeline

import Comment
import TimelineItem
import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Shader
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.challengeme.R
import com.example.challengeme.databinding.ItemTimelineBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import com.squareup.picasso.Transformation

class TimelineAdapter(
    private val timelineItems: List<TimelineItem>,
    private val fragment: Fragment
) : RecyclerView.Adapter<TimelineAdapter.TimelineViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimelineViewHolder {
        val binding = ItemTimelineBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TimelineViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TimelineViewHolder, position: Int) {
        val timelineItem = timelineItems[position]

        // ユーザー名をセット
        holder.binding.userName.text = timelineItem.userName

        // ユーザーアイコンを丸くして表示（さらに右に90度回転）
        if (!timelineItem.userIcon.isNullOrEmpty()) {
            Picasso.get().load(timelineItem.userIcon)
                .transform(CircleTransform()) // 丸く表示しつつ、画像を右に90度回転
                .into(holder.binding.userIcon)
        } else {
            // デフォルトアイコンを表示
            holder.binding.userIcon.setImageResource(R.drawable.profile)
        }

        // コメントをセット
        holder.binding.commentText.text = timelineItem.comment

        // 投稿画像を表示
        if (!timelineItem.imageUrl.isNullOrEmpty()) {
            Picasso.get().load(timelineItem.imageUrl).into(holder.binding.postImage)
        } else {
            holder.binding.postImage.setImageResource(R.drawable.default_image)
        }

        // いいねカウントを表示
        holder.binding.likeCountTextView.text = timelineItem.likeCount.toString()

        // コメント数を表示
        holder.binding.commentCountTextView.text = timelineItem.commentCount.toString()

        // いいねボタンの状態を確認してアイコンを設定
        checkIfLiked(holder, timelineItem)

        // いいねボタンのクリックリスナーを設定
        holder.binding.likeButton.setOnClickListener {
            toggleLike(holder, timelineItem)
        }

        // コメントアイコンがクリックされたときにダイアログを表示
        holder.binding.commentIcon.setOnClickListener {
            val commentsDialog = CommentsDialogFragment(timelineItem.timelineId)
            commentsDialog.show(fragment.childFragmentManager, "CommentsDialog")
        }

        // コメント送信ボタンが押されたときの処理
        holder.binding.commentSendButton.setOnClickListener {
            val commentText = holder.binding.commentEditText.text.toString()
            if (commentText.isNotEmpty()) {
                addCommentToPost(timelineItem, commentText, holder)
            }
        }
        // コメントをFirestoreから取得して表示
//        loadComments(holder, timelineItem)
    }

    override fun getItemCount(): Int = timelineItems.size

    // Firestoreにコメントを追加するメソッド
    private fun addCommentToPost(timelineItem: TimelineItem, commentText: String, holder: TimelineViewHolder) {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        val commentData = hashMapOf(
            "userId" to currentUserId,
            "commentText" to commentText,
            "commentedAt" to com.google.firebase.Timestamp.now()
        )

        val postRef = FirebaseFirestore.getInstance().collection("timeline").document(timelineItem.timelineId)
        postRef.collection("comments").add(commentData).addOnSuccessListener {
            // コメントが追加されたらcommentCountを増加
            postRef.update("commentCount", FieldValue.increment(1))

            // コメント欄をクリア
            holder.binding.commentEditText.text.clear()

            // コメント入力欄と送信ボタンを非表示に
            holder.binding.commentEditText.visibility = View.GONE
            holder.binding.commentSendButton.visibility = View.GONE
        }
    }

    // Firestoreからコメントを取得して表示するメソッド
    private fun loadComments(holder: TimelineViewHolder, timelineItem: TimelineItem) {
        val postRef = FirebaseFirestore.getInstance().collection("timeline").document(timelineItem.timelineId)
        postRef.collection("comments").orderBy("commentedAt").get().addOnSuccessListener { result ->
            val comments = mutableListOf<Comment>()
            for (document in result) {
                val commentText = document.getString("commentText") ?: ""
                val userId = document.getString("userId") ?: ""
                val commentedAt = document.getTimestamp("commentedAt")

                comments.add(Comment(userId, commentText, commentedAt))
            }

            // コメントが存在する場合のみRecyclerViewを表示
            if (comments.isNotEmpty()) {
                holder.binding.commentsRecyclerView.visibility = View.VISIBLE
                val commentAdapter = CommentAdapter(comments)
                holder.binding.commentsRecyclerView.layoutManager = LinearLayoutManager(holder.itemView.context)
                holder.binding.commentsRecyclerView.adapter = commentAdapter
            } else {
                holder.binding.commentsRecyclerView.visibility = View.GONE
            }
        }
    }

    // いいねの状態を確認してアイコンを更新するメソッド
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

    // Picasso 用の丸い画像変換クラス（右に90度回転させる）
    class CircleTransform : Transformation {
        override fun transform(source: Bitmap): Bitmap {
            val size = Math.min(source.width, source.height)
            val x = (source.width - size) / 2
            val y = (source.height - size) / 2
            val squaredBitmap = Bitmap.createBitmap(source, x, y, size, size)
            if (squaredBitmap != source) {
                source.recycle()
            }

            // ビットマップを右に90度回転させる
            val matrix = Matrix()
            matrix.postRotate(90f) // 右に90度回転

            val rotatedBitmap = Bitmap.createBitmap(squaredBitmap, 0, 0, size, size, matrix, true)

            val bitmap = Bitmap.createBitmap(size, size, source.config)
            val canvas = Canvas(bitmap)
            val paint = Paint()
            val shader = BitmapShader(rotatedBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
            paint.shader = shader
            paint.isAntiAlias = true
            val r = size / 2f
            canvas.drawCircle(r, r, r, paint)
            squaredBitmap.recycle()
            return bitmap
        }

        override fun key(): String {
            return "circle_with_rotation"
        }
    }
}
