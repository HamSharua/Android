package com.example.challengeme

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.util.*

class RegisterActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage

    private val PICK_IMAGE_REQUEST = 1
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        val iconImageView = findViewById<ImageView>(R.id.iconImageView)
        iconImageView.setOnClickListener {
            openImagePicker()  // 画像選択インテントを起動
        }

        val registerButton = findViewById<Button>(R.id.registerButton)
        registerButton.setOnClickListener {
            registerUser()
        }
    }

    // ギャラリーから画像を選択するメソッド
    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    // 画像が選択された場合の処理
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            imageUri = data.data
            val iconImageView = findViewById<ImageView>(R.id.iconImageView)
            iconImageView.setImageURI(imageUri)  // 選択された画像をImageViewにセット
        }
    }

    private fun registerUser() {
        val email = findViewById<EditText>(R.id.emailEditText).text.toString()
        val password = findViewById<EditText>(R.id.passwordEditText).text.toString()
        val name = findViewById<EditText>(R.id.nameEditText).text.toString()
        val iconImageView = findViewById<ImageView>(R.id.iconImageView)

        if (email.isEmpty() || password.isEmpty() || name.isEmpty()) {
            Toast.makeText(this, "すべての必須項目を入力してください", Toast.LENGTH_SHORT).show()
            return
        }

        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val userId = firebaseAuth.currentUser!!.uid
                val registrationDate = Date()

                // アイコンのアップロード
                if (imageUri != null) {
                    uploadIconAndSaveUser(imageUri!!, userId, email, name, password, registrationDate)
                } else {
                    // アイコンがない場合もFirestoreにデータを保存
                    saveUserToFirestore(userId, email, name, password, registrationDate, null)
                }
            } else {
                Toast.makeText(this, "登録に失敗しました: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // アイコンをFirebase Storageにアップロードし、Firestoreにデータを保存
    private fun uploadIconAndSaveUser(
        uri: Uri,
        userId: String,
        email: String,
        name: String,
        password: String,
        registrationDate: Date
    ) {
        val iconRef = storage.reference.child("user_icons/${UUID.randomUUID()}.png")
        val uploadTask = iconRef.putFile(uri)

        uploadTask.addOnSuccessListener {
            iconRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                saveUserToFirestore(userId, email, name, password, registrationDate, downloadUrl.toString())
            }.addOnFailureListener { e ->
                Toast.makeText(this, "アイコンURL取得に失敗しました: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { e ->
            Toast.makeText(this, "アイコンのアップロードに失敗しました: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    // Firestoreにユーザーデータを保存
    private fun saveUserToFirestore(
        userId: String,
        email: String,
        name: String,
        password: String,
        registrationDate: Date,
        iconUrl: String?
    ) {
        val userMap = hashMapOf(
            "challenge_num" to "all",
            "registration_date" to registrationDate,
            "user_address" to email,
            "user_id" to userId,
            "user_name" to name,
            "user_pass" to password
        )

        iconUrl?.let {
            userMap["user_icon"] = it
        }

        firestore.collection("users").document(userId).set(userMap)
            .addOnSuccessListener {
                Toast.makeText(this, "登録が成功しました", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Firestoreへの保存に失敗しました: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
