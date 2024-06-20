package com.example.app

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.challengeme.R


class ChallengeCameraFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // フラグメントのレイアウトをインフレート
        return inflater.inflate(R.layout.fragment_challenge_camera, container, false)
    }

    // カメラ機能の実装をここに追加します
}
