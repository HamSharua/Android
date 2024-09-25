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
import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast

class ChallengeFragment : Fragment() {

    private var _binding: FragmentChallengeBinding? = null
    private val binding get() = _binding!!

    private lateinit var firestore: FirebaseFirestore
    private lateinit var challengeContent: TextView
    private lateinit var challengeImage: ImageView
    private lateinit var challengeButton: Button
    private lateinit var changeChallengeButton: Button
    private lateinit var remainingChangesTextView: TextView
    private lateinit var sharedPreferences: SharedPreferences
    private var selectedChallengeId: Long? = null

    // 1日の最大変更回数
    private val MAX_CHANGES_PER_DAY = 3

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChallengeBinding.inflate(inflater, container, false)
        val view = binding.root

        firestore = FirebaseFirestore.getInstance()
        sharedPreferences = requireContext().getSharedPreferences("challenge_prefs", Context.MODE_PRIVATE)

        challengeContent = binding.challengeContent
        challengeImage = binding.challengeImage
        challengeButton = binding.btnChallenge
        changeChallengeButton = binding.btnChangeChallenge
        remainingChangesTextView = binding.tvRemainingChanges

        updateRemainingChangesText() // 残り回数を表示

        // Restore challenge and button state from SharedPreferences
        updateChangeChallengeButtonState()

        val savedChallengeContent = sharedPreferences.getString("current_challenge_content", null)
        val savedChallengeImage = sharedPreferences.getString("current_challenge_image", null)
        val savedChallengeId = sharedPreferences.getLong("current_challenge_id", -1L) // challenge_id を保存・復元

        if (savedChallengeContent != null && savedChallengeImage != null && savedChallengeId != -1L) {
            challengeContent.text = savedChallengeContent
            Picasso.get().load(savedChallengeImage).into(challengeImage)
            selectedChallengeId = savedChallengeId
        } else {
            fetchRandomChallenge()  // Fetch a new challenge if none is saved
        }

        changeChallengeButton.setOnClickListener {
            if (canChangeChallenge()) {
                fetchRandomChallenge()
                incrementChangeCount()
                updateRemainingChangesText() // 残り回数を更新
                updateChangeChallengeButtonState() // ボタン状態を更新
            } else {
                updateChangeChallengeButtonState() // ボタン状態を更新
            }
        }

        challengeButton.setOnClickListener {
            if (selectedChallengeId != null) {
                // selectedChallengeId が null でない場合にナビゲート
                val action = ChallengeFragmentDirections.actionChallengeFragmentToChallengeCameraFragment(selectedChallengeId!!)
                findNavController().navigate(action)
            } else {
                // selectedChallengeId が null の場合にエラーメッセージを表示
                Toast.makeText(requireContext(), "チャレンジが選択されていません", Toast.LENGTH_SHORT).show()
            }
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
                    val challengeContentText = randomChallenge.getString("challenge_content")
                    val imageUrl = randomChallenge.getString("challenge_image")
                    selectedChallengeId = randomChallenge.getLong("challenge_id") // 選択されたチャレンジIDを保存

                    if (selectedChallengeId != null) {
                        // Save challenge to SharedPreferences
                        sharedPreferences.edit()
                            .putString("current_challenge_content", challengeContentText)
                            .putString("current_challenge_image", imageUrl)
                            .putLong("current_challenge_id", selectedChallengeId ?: -1L) // challenge_id を保存
                            .apply()

                        // Update UI
                        challengeContent.text = challengeContentText
                        Picasso.get().load(imageUrl).into(challengeImage)
                    } else {
                        Toast.makeText(requireContext(), "チャレンジIDが取得できませんでした", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "チャレンジが見つかりませんでした", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "チャレンジの取得に失敗しました", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateRemainingChangesText() {
        val remainingChanges = MAX_CHANGES_PER_DAY - sharedPreferences.getInt("change_count", 0)
        remainingChangesTextView.text = "残り変更回数: $remainingChanges"
    }

    private fun canChangeChallenge(): Boolean {
        val currentDate = getCurrentDate()
        val lastChangeDate = sharedPreferences.getString("last_change_date", "")
        val changeCount = sharedPreferences.getInt("change_count", 0)

        return if (currentDate != lastChangeDate) {
            resetChangeCount()
            true
        } else {
            changeCount < MAX_CHANGES_PER_DAY
        }
    }

    private fun incrementChangeCount() {
        val currentDate = getCurrentDate()
        var changeCount = sharedPreferences.getInt("change_count", 0)
        changeCount++

        sharedPreferences.edit()
            .putString("last_change_date", currentDate)
            .putInt("change_count", changeCount)
            .apply()
    }

    private fun resetChangeCount() {
        sharedPreferences.edit()
            .putString("last_change_date", getCurrentDate())
            .putInt("change_count", 0)
            .apply()
    }

    private fun getCurrentDate(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = java.text.SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    private fun updateChangeChallengeButtonState() {
        if (canChangeChallenge()) {
            changeChallengeButton.isEnabled = true
            changeChallengeButton.text = "チャレンジ変更"
        } else {
            changeChallengeButton.isEnabled = false
            changeChallengeButton.text = "変更不可"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
