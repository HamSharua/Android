package com.example.challengeme.ui.profile

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.example.challengeme.LoginActivity
import com.example.challengeme.ui.profile.ProfileEditActivity  // プロフィール編集画面へのインポート
import com.example.challengeme.R
import com.example.challengeme.RegisterActivity
import com.example.challengeme.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        return root
    }

    override fun onResume() {
        super.onResume()
        // 画面に戻ってきた際に最新のプロフィールデータを取得
        loadProfileData()
    }

    private fun loadProfileData() {
        val currentUser = firebaseAuth.currentUser

        if (currentUser != null) {
            // Firestoreからユーザーデータを取得
            val userId = currentUser.uid
            firestore.collection("users").document(userId).get().addOnSuccessListener { document ->
                if (document != null) {
                    val userName = document.getString("user_name") ?: "N/A"
                    val userEmail = document.getString("user_address") ?: "N/A"
                    val userIconUrl = document.getString("user_icon")

                    // プロフィールの各項目にデータを設定
                    binding.usernameTextView.text = userName
                    binding.emailTextView.text = userEmail
                    binding.passwordTextView.text = "********"  // パスワードは非表示で固定

                    // Glideでアイコンを円形に表示
                    userIconUrl?.let {
                        Glide.with(this)
                            .load(it)
                            .circleCrop()
                            .into(binding.profileImageView)
                    }

                    // アイコンをクリックしたらポップアップで全体表示
                    binding.profileImageView.setOnClickListener {
                        showIconPopup(userIconUrl)
                    }
                }
            }.addOnFailureListener { e ->
                Toast.makeText(context, "データの取得に失敗しました: ${e.message}", Toast.LENGTH_SHORT).show()
            }

            // プロフィール変更ボタンの動作設定
            binding.buttonEditProfile.setOnClickListener {
                Log.d("ProfileFragment", "プロフィール変更ボタンが押されました")
                val intent = Intent(requireContext(), ProfileEditActivity::class.java)
                startActivity(intent) // プロフィール編集画面に遷移
            }

            // ログアウトボタンの動作設定
            binding.logoutButton.setOnClickListener {
                FirebaseAuth.getInstance().signOut()
                Toast.makeText(context, "ログアウトしました", Toast.LENGTH_SHORT).show()

                // ログアウト後に画面を更新
                requireActivity().recreate()
            }

        } else {
            // ログインしていない場合はダイアログを表示
            showLoginDialog()
        }
    }

    // アイコンのポップアップ表示
    private fun showIconPopup(imageUrl: String?) {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_image_popup)
        val popupImageView = dialog.findViewById<ImageView>(R.id.popupImageView)

        // Glideで画像をロード
        imageUrl?.let {
            Glide.with(this).load(it).into(popupImageView)
        }

        popupImageView.setOnClickListener {
            dialog.dismiss()  // ポップアップを閉じる
        }

        dialog.show()
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
