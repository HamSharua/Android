package com.example.challengeme

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        firebaseAuth = FirebaseAuth.getInstance()

        val usernameEditText = findViewById<EditText>(R.id.editTextUsername)
        val passwordEditText = findViewById<EditText>(R.id.editTextPassword)
        val loginButton = findViewById<Button>(R.id.buttonLogin)
        val backButton = findViewById<Button>(R.id.buttonBack)

        // 戻るボタンの処理
        backButton.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        // ログインボタンの処理
        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (username.isEmpty()) {
                showDialog("ユーザー名を入力してください")
            } else if (password.isEmpty()) {
                showDialog("パスワードを入力してください")
            } else {
                // ダミーのメールアドレスを生成
                val email = "$username@dummy.com"

                // FirebaseAuthでのログイン処理
                firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // ログイン成功
                            showDialog("ログイン成功！")
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        } else {
                            // ログイン失敗
                            showDialog("ログインに失敗しました: ${task.exception?.message}")
                        }
                    }
            }
        }
    }

    // ダイアログを表示する共通メソッド
    private fun showDialog(message: String) {
        AlertDialog.Builder(this)
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .show()
    }
}
