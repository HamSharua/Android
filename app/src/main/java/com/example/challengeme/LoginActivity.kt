package com.example.challengeme

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // FirestoreとFirebaseAuthの初期化
        firestore = FirebaseFirestore.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()

        // UI要素の取得
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
                // Firestoreでユーザー名からメールアドレスを取得し、FirebaseAuthでログイン
                loginUser(username, password)
            }
        }
    }

    // Firestoreでユーザー名からメールアドレスを取得し、FirebaseAuthでログイン
    private fun loginUser(username: String, password: String) {
        firestore.collection("users")
            .whereEqualTo("user_name", username)  // user_name が一致するかチェック
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (!task.result.isEmpty) {
                        // ユーザー名に一致するユーザーが存在する場合、メールアドレスを取得
                        val email = task.result.documents[0].getString("user_address") ?: ""
                        // FirebaseAuthでのログイン処理
                        firebaseAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener { authTask ->
                                if (authTask.isSuccessful) {
                                    // ログイン成功
                                    showDialog("ログイン成功！")
                                    startActivity(Intent(this, MainActivity::class.java))
                                    finish()
                                } else {
                                    // ログイン失敗
                                    showDialog("ログインに失敗しました: ${authTask.exception?.message}")
                                }
                            }
                    } else {
                        // 一致するユーザーがいない場合
                        showDialog("ユーザー名またはパスワードが正しくありません")
                    }
                } else {
                    // Firestoreクエリ失敗
                    showDialog("データベースエラー: ${task.exception?.message}")
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
