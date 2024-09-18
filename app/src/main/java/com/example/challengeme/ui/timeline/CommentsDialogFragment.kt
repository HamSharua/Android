package com.example.challengeme.ui.timeline

import Comment
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.challengeme.databinding.FragmentCommentsDialogBinding
import com.example.challengeme.ui.timeline.CommentAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CommentsDialogFragment(private val timelineId: String) : DialogFragment() {

    private var _binding: FragmentCommentsDialogBinding? = null
    private val binding get() = _binding!!

    private val comments = mutableListOf<Comment>()
    private lateinit var adapter: CommentAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCommentsDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // アダプタを設定
        adapter = CommentAdapter(comments)
        binding.commentsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.commentsRecyclerView.adapter = adapter

        // Firestoreからコメントを取得
        FirebaseFirestore.getInstance().collection("timeline")
            .document(timelineId).collection("comments")
            .orderBy("commentedAt").get().addOnSuccessListener { result ->
                comments.clear() // 以前のデータをクリア
                for (document in result) {
                    val commentText = document.getString("commentText") ?: ""
                    val userId = document.getString("userId") ?: ""
                    val commentedAt = document.getTimestamp("commentedAt")

                    comments.add(Comment(userId, commentText, commentedAt))
                }
                adapter.notifyDataSetChanged() // データが変更されたことを通知
            }

        // コメント送信ボタンのクリックリスナー
        binding.commentSendButton.setOnClickListener {
            val commentText = binding.commentEditText.text.toString()
            if (commentText.isNotEmpty()) {
                addCommentToPost(commentText)
            }
        }
    }

    private fun addCommentToPost(commentText: String) {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        val commentData = hashMapOf(
            "userId" to currentUserId,
            "commentText" to commentText,
            "commentedAt" to com.google.firebase.Timestamp.now()
        )

        FirebaseFirestore.getInstance().collection("timeline")
            .document(timelineId).collection("comments").add(commentData)
            .addOnSuccessListener {
                binding.commentEditText.text.clear()
                // 新しいコメントを表示
                comments.add(Comment(currentUserId ?: "", commentText, com.google.firebase.Timestamp.now()))
                adapter.notifyItemInserted(comments.size - 1)
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onStart() {
        super.onStart()
        // ウィンドウサイズを調整
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            (resources.displayMetrics.heightPixels * 0.66).toInt()  // 画面下2/3程度
        )
        dialog?.window?.setGravity(Gravity.BOTTOM)  // ウィンドウを画面下方に固定
    }
}

