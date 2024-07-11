package com.example.challengeme.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.challengeme.R
import com.example.challengeme.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val profileViewModel =
            ViewModelProvider(this).get(ProfileViewModel::class.java)

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val profileImage: ImageView = binding.profileImage
        val editUsername: EditText = binding.editUsername
        val editEmail: EditText = binding.editEmail
        val buttonSave: Button = binding.buttonSave

        profileViewModel.text.observe(viewLifecycleOwner) {
            // Optional: Update UI with ViewModel data if needed
        }

        buttonSave.setOnClickListener {
            val username = editUsername.text.toString()
            val email = editEmail.text.toString()

            // Save the data (implement the saving logic here)
            // For now, we'll just show a Toast as a placeholder
            // Toast.makeText(context, "Saved: $username, $email", Toast.LENGTH_SHORT).show()
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
