package com.example.challengeme

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class RegisterActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val registerButton = findViewById<Button>(R.id.registerButton)
        registerButton.setOnClickListener {
            registerUser()
        }
    }

    private fun registerUser() {
        val name = findViewById<EditText>(R.id.nameEditText).text.toString()
        val password = findViewById<EditText>(R.id.passwordEditText).text.toString()

        if (name.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "すべての必須項目を入力してください", Toast.LENGTH_SHORT).show()
            return
        }

        // ダミーのメールアドレスを生成
        val email = "$name@dummy.com"

        // FirebaseAuthでのユーザー登録処理
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val userId = firebaseAuth.currentUser!!.uid
                val registrationDate = Date()

                // Firestoreにユーザーデータを保存
                saveUserToFirestore(userId, email, name, password, registrationDate)
            } else {
                Toast.makeText(this, "登録に失敗しました: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Firestoreにユーザーデータを保存するメソッド
    private fun saveUserToFirestore(
        userId: String,
        email: String,
        name: String,
        password: String,
        registrationDate: Date
    ) {
        val userMap = hashMapOf(
            "user_address" to email,
            "user_name" to name,
            "user_pass" to password,
            "registration_date" to registrationDate
        )

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
