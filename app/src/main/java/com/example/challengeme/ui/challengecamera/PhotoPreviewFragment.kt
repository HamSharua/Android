package com.example.challengeme.ui.challengecamera

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.challengeme.R
import com.example.challengeme.databinding.FragmentPhotoPreviewBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class PhotoPreviewFragment : Fragment() {

    private var _binding: FragmentPhotoPreviewBinding? = null
    private val binding get() = _binding!!
    private val args: PhotoPreviewFragmentArgs by navArgs()
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val auth = FirebaseAuth.getInstance() // FirebaseAuthのインスタンスを取得


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPhotoPreviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 渡された画像パスを使用して画像を表示
        val photoFile = File(args.photoPath)
        if (photoFile.exists()) {
            val uri = Uri.fromFile(photoFile)
            binding.photoView.setImageURI(uri)
        } else {
            Toast.makeText(requireContext(), "Failed to load image", Toast.LENGTH_SHORT).show()
        }

        binding.emojiIcon.setOnClickListener {
            val comment = binding.editTextPost.text.toString()
            if (comment.isEmpty()) {
                Toast.makeText(requireContext(), "Comment cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            uploadImageAndSaveData(photoFile, comment)
        }
    }

    private fun uploadImageAndSaveData(photoFile: File, comment: String) {
        val uri = Uri.fromFile(photoFile)
        val storageRef = storage.reference.child("images/${UUID.randomUUID()}.png")
        val uploadTask = storageRef.putFile(uri)

        uploadTask.addOnSuccessListener {
            storageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                saveDataToFirestore(downloadUrl.toString(), comment)
            }.addOnFailureListener { e ->
                Log.e("PhotoPreviewFragment", "Failed to get download URL", e)
                Toast.makeText(requireContext(), "Failed to get download URL", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { e ->
            Log.e("PhotoPreviewFragment", "Image upload failed", e)
            Toast.makeText(requireContext(), "Image upload failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveDataToFirestore(imageUrl: String, comment: String) {
        // ログインしているユーザーの情報を取得
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        val data = hashMapOf(
            "challenge_id" to args.challengeId, // ChallengeFragmentから渡されたchallenge_idを使用
            "comment" to comment,
            "datetime" to Date(),
            "image" to imageUrl,
            "timeline_id" to UUID.randomUUID().toString(),
            "user_id" to currentUser.uid // ログインしているユーザーのuser_idを使用
        )

        db.collection("timeline")
            .add(data)
            .addOnSuccessListener {
                // カレンダーに投稿されたコメントを保存
                val date = SimpleDateFormat("dd/MM/yyyy", Locale.JAPAN).format(Date())
                val sharedPref = requireActivity().getSharedPreferences("calenderNotes", Context.MODE_PRIVATE)
                with(sharedPref.edit()) {
                    putString(date, comment)  // コメントを保存
                    apply()
                }

                Toast.makeText(requireContext(), "Post successful", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_photoPreviewFragment_to_challengeFragment)
            }
            .addOnFailureListener { e ->
                Log.e("PhotoPreviewFragment", "Error saving data", e)
                Toast.makeText(requireContext(), "Failed to save post", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
