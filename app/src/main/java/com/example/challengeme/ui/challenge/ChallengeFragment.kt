package com.example.challengeme.ui.challenge

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.challengeme.R
import com.example.challengeme.databinding.FragmentChallengeBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import java.util.*

class ChallengeFragment : Fragment() {

    private var _binding: FragmentChallengeBinding? = null
    private val binding get() = _binding!!

    private lateinit var firestore: FirebaseFirestore
    private lateinit var challengeContent: TextView
    private lateinit var challengeImage: ImageView
    private lateinit var challengeButton: Button
    private lateinit var changeChallengeButton: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChallengeBinding.inflate(inflater, container, false)
        val view = binding.root

        firestore = FirebaseFirestore.getInstance()
        challengeContent = binding.challengeContent
        challengeImage = binding.challengeImage
        challengeButton = binding.btnChallenge
        changeChallengeButton = binding.btnChangeChallenge

        fetchRandomChallenge()

        changeChallengeButton.setOnClickListener {
            fetchRandomChallenge()
        }

        challengeButton.setOnClickListener {
            // ChallengeCameraFragmentへ遷移
            findNavController().navigate(R.id.action_challengeFragment_to_challengeCameraFragment)
        }

        return view
    }

    private fun fetchRandomChallenge() {
        firestore.collection("challenge")
            .get()
            .addOnSuccessListener { result ->
                val challenges = result.documents
                if (challenges.isNotEmpty()) {
                    val randomChallenge = challenges[Random().nextInt(challenges.size)]
                    challengeContent.text = randomChallenge.getString("challenge_content")
                    val imageUrl = randomChallenge.getString("challenge_image")
                    Picasso.get().load(imageUrl).into(challengeImage)
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
