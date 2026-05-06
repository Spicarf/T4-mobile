package com.example.studentcontactapp.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.example.studentcontactapp.databinding.FragmentProfileBinding
import com.example.studentcontactapp.ui.login.LoginActivity
import com.example.studentcontactapp.utils.PrefManager
import com.example.studentcontactapp.utils.SettingsManager

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var prefManager: PrefManager
    private lateinit var settingsManager: SettingsManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prefManager = PrefManager(requireContext())
        settingsManager = SettingsManager(requireContext())

        setupUI()
        setupListeners()
    }

    private fun setupUI() {
        binding.tvUsername.text = prefManager.getUsername()

        // Dark Mode
        binding.switchDarkMode.isChecked = settingsManager.isDarkMode

        // Font Size label
        updateFontSizeLabel()

        // Notifikasi : Coming Soon
        binding.switchNotification.isEnabled = false
        binding.switchNotification.isChecked = false
        binding.switchNotification.text = "Coming Soon"
    }

    private fun updateFontSizeLabel() {
        val label = when (settingsManager.fontScale) {
            0.8f -> "Small"
            1.0f -> "Medium"
            1.2f -> "Large"
            else -> "Medium"
        }
        binding.tvFontSizeValue.text = label
    }

    private fun setupListeners() {
        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            settingsManager.isDarkMode = isChecked
            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )
            requireActivity().recreate()
        }

        // Switch notifikasi dinonaktifkan, tidak perlu listener

        binding.layoutFontSize.setOnClickListener {
            showFontSizeDialog()
        }

        binding.btnLogout.setOnClickListener {
            prefManager.logout()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish()
        }
    }

    private fun showFontSizeDialog() {
        val items = arrayOf("Small (0.8x)", "Medium (1.0x - Default)", "Large (1.2x)")
        val scales = floatArrayOf(0.8f, 1.0f, 1.2f)
        val currentScale = settingsManager.fontScale
        val checkedItem = when (currentScale) {
            0.8f -> 0
            1.0f -> 1
            1.2f -> 2
            else -> 1
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Pilih Ukuran Font")
            .setSingleChoiceItems(items, checkedItem) { dialog, which ->
                settingsManager.fontScale = scales[which]
                updateFontSizeLabel()
                dialog.dismiss()
                requireActivity().recreate()
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}