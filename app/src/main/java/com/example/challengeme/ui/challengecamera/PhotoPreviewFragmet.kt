package com.example.challengeme.ui.challengecamera

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.challengeme.databinding.FragmentPhotoPreviewBinding
import java.io.File

class PhotoPreviewFragment : Fragment() {

    private var _binding: FragmentPhotoPreviewBinding? = null
    private val binding get() = _binding!!
    private val args: PhotoPreviewFragmentArgs by navArgs()

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

        binding.retakeButton.setOnClickListener {
            // 再撮影ボタンの処理
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
