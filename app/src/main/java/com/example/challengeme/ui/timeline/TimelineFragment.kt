package com.example.challengeme.ui.timeline

import TimelineItem
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.challengeme.databinding.FragmentTimelineBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import android.app.AlertDialog
import android.content.Intent
import androidx.navigation.findNavController
import com.example.challengeme.LoginActivity
import com.example.challengeme.RegisterActivity
import com.example.challengeme.R

class TimelineFragment : Fragment() {

    private var _binding: FragmentTimelineBinding? = null
    private val binding get() = _binding!!
    private val firestore = FirebaseFirestore.getInstance()
    private lateinit var auth: FirebaseAuth  // FirebaseAuthインスタンス

    // タイムラインデータを保持するリスト
    private val timelineItems = mutableListOf<TimelineItem>()
    private lateinit var timelineAdapter: TimelineAdapter  // アダプターをここで定義

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTimelineBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // FirebaseAuthのインスタンスを取得
        auth = FirebaseAuth.getInstance()

        // RecyclerView のレイアウトマネージャーを設定
        binding.timelineRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // アダプタの設定（初期化時にアダプタを作成）
        timelineAdapter = TimelineAdapter(timelineItems, this@TimelineFragment)
        binding.timelineRecyclerView.adapter = timelineAdapter  // アダプタをセット

        // onViewCreatedでは、ポップアップを表示しない
        // データ取得はonResume()で行う
    }

    override fun onResume() {
        super.onResume()
        // ページに戻ってきたときに再度ログイン状態を確認し、データを取得
        checkLoginStatusAndFetchData()
    }

    private fun checkLoginStatusAndFetchData() {
        val currentUser = auth.currentUser

        if (currentUser != null) {
            // ユーザーがログインしている場合、タイムラインデータを取得
            fetchTimelineData()
        } else {
            // ユーザーがログインしていない場合、ログインダイアログを表示
            showLoginDialog()
        }
    }

    private fun fetchTimelineData() {
        firestore.collection("timeline")
            .orderBy("datetime", Query.Direction.DESCENDING)  // 新しい順に並べ替え
            .get()
            .addOnSuccessListener { result ->
                timelineItems.clear()  // 既存のデータをクリア
                for (document in result) {
                    val userId = document.getString("user_id") ?: ""
                    val comment = document.getString("comment") ?: ""
                    val imageUrl = document.getString("image") ?: ""
                    val likeCount = document.getLong("likeCount") ?: 0
                    val commentCount = document.getLong("commentCount") ?: 0
                    val datetime = document.getTimestamp("datetime")  // datetime を取得
                    val challengeId = document.getLong("challenge_id") ?: 0  // challenge_id を取得

                    firestore.collection("users").document(userId).get()
                        .addOnSuccessListener { userDoc ->
                            val userName = userDoc.getString("user_name") ?: ""
                            val userIcon = userDoc.getString("user_icon") ?: ""

                            val timelineItem = TimelineItem(
                                timelineId = document.id,
                                userId = userId,
                                userName = userName,
                                userIcon = userIcon,
                                comment = comment,
                                imageUrl = imageUrl,
                                likeCount = likeCount,
                                commentCount = commentCount,
                                datetime = datetime,  // 取得した datetime をセット
                                challengeId = challengeId  // challengeIdをセット
                            )
                            timelineItems.add(timelineItem)

                            // データ変更を通知
                            timelineAdapter.notifyDataSetChanged()  // データの更新を通知
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error fetching timeline data", e)
            }
    }

    // ログインが必要な場合のダイアログ表示
    private fun showLoginDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("ログインする必要があります")
        builder.setMessage("まだログインしていない方は新規登録を行ってください。")

        // ログインボタン
        builder.setPositiveButton("ログイン") { _, _ ->
            // ログイン画面へ遷移（LoginActivityを作成している場合）
            val intent = Intent(activity, LoginActivity::class.java)
            startActivity(intent)
        }

        // 新規登録ボタン
        builder.setNegativeButton("新規登録") { _, _ ->
            val intent = Intent(activity, RegisterActivity::class.java)
            startActivity(intent)
        }

        builder.setNeutralButton("キャンセル") { dialog, _ ->
            // Challengeページにナビゲート
            val navController = requireActivity().findNavController(R.id.nav_host_fragment_activity_main)
            navController.navigate(R.id.navigation_challenge)  // Challengeページに遷移
            dialog.dismiss()
        }
        val dialog = builder.create()
        // ポップアップ外をタップしても閉じないようにする
        dialog.setCancelable(false)
        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
