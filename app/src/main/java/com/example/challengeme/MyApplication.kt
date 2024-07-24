//package com.example.challengeme
//
//import android.app.Application
//import com.google.firebase.FirebaseApp
//
//class MyApplication : Application() {
//    override fun onCreate() {
//        super.onCreate()
//        FirebaseApp.initializeApp(this)
//    }
//}
package com.example.challengeme

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)

        // サインインしているか確認
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            // サインインしていない場合はサインイン処理を行う
            signInAnonymously()
        }
    }

    private fun signInAnonymously() {
        FirebaseAuth.getInstance().signInAnonymously()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // サインイン成功
                    val user = FirebaseAuth.getInstance().currentUser
                    // ここで必要な操作を行う
                } else {
                    // サインイン失敗
                    task.exception?.printStackTrace()
                }
            }
    }
}
