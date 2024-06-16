package com.example.challengeme.ui.challenge

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.challengeme.databinding.FragmentChallengeBinding
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.example.challengeme.R

class ChallengeFragment : Fragment() {

    private var _binding: FragmentChallengeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
//        val challengeViewModel =
//            ViewModelProvider(this).get(ChallengeViewModel::class.java)
//
//        _binding = FragmentChallengeBinding.inflate(inflater, container, false)
//        val root: View = binding.root
//
//        val textView: TextView = binding.textChallenge
//        challengeViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }
//        return root
        // フラグメントのレイアウトをインフレート
        val view = inflater.inflate(R.layout.fragment_challenge, container, false)

        // ボタンを取得してクリックリスナーを設定
        val challengeButton: Button = view.findViewById(R.id.btn_challenge)
        challengeButton.setOnClickListener {
            // ChallengeCameraFragmentへ遷移
            findNavController().navigate(R.id.action_challengeFragment_to_challengeCameraFragment)
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}