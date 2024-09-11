package com.example.challengeme.ui.profile

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.challengeme.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream

class ProfileEditActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage

    private var imageUri: Uri? = null
    private val PICK_IMAGE_REQUEST = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_edit)

        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        // UIコンポーネントの参照を取得
        val usernameEditText = findViewById<EditText>(R.id.editTextUsername)
        val emailEditText = findViewById<EditText>(R.id.editTextEmail)
//        val passwordEditText = findViewById<EditText>(R.id.editTextPassword)
        val iconImageView = findViewById<ImageView>(R.id.editProfileImageView)
        val saveButton = findViewById<Button>(R.id.buttonSave)

        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid

            // Firestoreから現在のユーザー情報を取得してUIに反映
            firestore.collection("users").document(userId).get().addOnSuccessListener { document ->
                if (document != null) {
                    usernameEditText.setText(document.getString("user_name"))
                    emailEditText.setText(document.getString("user_address"))
                    // Glideで現在のアイコンを表示
                    val userIconUrl = document.getString("user_icon")
                    Glide.with(this)
                        .load(userIconUrl)
                        .placeholder(R.drawable.profile) // デフォルト画像
                        .circleCrop()
                        .into(iconImageView)
                }
            }

            // アイコン変更のため、ImageViewクリックで画像選択
            iconImageView.setOnClickListener {
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/*"
                startActivityForResult(intent, PICK_IMAGE_REQUEST)
            }

            // 保存ボタンの処理
            saveButton.setOnClickListener {
                val newUsername = usernameEditText.text.toString()
                val newEmail = emailEditText.text.toString()
//                val newPassword = passwordEditText.text.toString()

                if (newUsername.isNotEmpty() && newEmail.isNotEmpty()) {
                    // ユーザー情報をFireStoreに保存
                    val userMap = hashMapOf(
                        "user_name" to newUsername,
                        "user_address" to newEmail
                    )

                    // アイコンが選択されている場合、Firebase Storageにアップロード
                    if (imageUri != null) {
                        uploadIconToFirebase(imageUri!!, userId) { downloadUrl ->
                            userMap["user_icon"] = downloadUrl
                            updateFirestore(userId, userMap)
                        }
                    } else {
                        updateFirestore(userId, userMap)
                    }

                    // パスワード変更処理
//                    if (newPassword.isNotEmpty()) {
//                        currentUser.updatePassword(newPassword)
//                            .addOnSuccessListener {
//                                Toast.makeText(this, "パスワードが変更されました", Toast.LENGTH_SHORT).show()
//                            }
//                            .addOnFailureListener { e ->
//                                Toast.makeText(this, "パスワード変更に失敗しました: ${e.message}", Toast.LENGTH_SHORT).show()
//                            }
//                    }
                } else {
                    Toast.makeText(this, "ユーザー名とメールアドレスを入力してください", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // 画像選択後の処理
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            imageUri = data.data
            val iconImageView = findViewById<ImageView>(R.id.editProfileImageView)
            iconImageView.setImageURI(imageUri)
        }
    }

    // Firebase Storageにアイコン画像をアップロード
    private fun uploadIconToFirebase(uri: Uri, userId: String, callback: (String) -> Unit) {
        val iconRef = storage.reference.child("user_icons/$userId.png")
        iconRef.putFile(uri).addOnSuccessListener {
            iconRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                callback(downloadUrl.toString())
            }.addOnFailureListener {
                Toast.makeText(this, "アイコンURLの取得に失敗しました", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "アイコンのアップロードに失敗しました", Toast.LENGTH_SHORT).show()
        }
    }

    // Firestoreのデータを更新
    private fun updateFirestore(userId: String, userMap: Map<String, Any>) {
        firestore.collection("users").document(userId).update(userMap)
            .addOnSuccessListener {
                Toast.makeText(this, "プロフィールが更新されました", Toast.LENGTH_SHORT).show()
                finish() // 編集完了後に画面を終了
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Firestore更新に失敗しました: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
