package com.batspace.app.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.batspace.app.BuildConfig
import com.batspace.app.databinding.FragmentSettingsBinding
import com.batspace.app.data.prefs.ClonePreferences
import com.batspace.app.data.repository.CloneRepository

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val prefs = ClonePreferences(requireContext())
        val repo = CloneRepository(requireContext())

        binding.tvVersion.text = "v${BuildConfig.VERSION_NAME}"
        binding.tvTotalClones.text = "${prefs.getAllClones().size} active clones"

        binding.btnSetPin.setOnClickListener {
            showSetPinDialog(prefs, repo)
        }

        binding.btnClearAll.setOnClickListener {
            showClearAllDialog(prefs)
        }

        binding.switchDarkMode.isChecked = true
        binding.switchDarkMode.setOnCheckedChangeListener { _, _ ->
            Toast.makeText(requireContext(), "Dark mode is always on for Bat Space", Toast.LENGTH_SHORT).show()
            binding.switchDarkMode.isChecked = true
        }
    }

    private fun showSetPinDialog(prefs: ClonePreferences, repo: CloneRepository) {
        val input = android.widget.EditText(requireContext())
        input.inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_VARIATION_PASSWORD
        input.hint = "Enter 4-digit PIN"
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Set Global PIN")
            .setView(input)
            .setPositiveButton("Set") { _, _ ->
                val pin = input.text.toString()
                if (pin.length >= 4) {
                    prefs.setGlobalPin(repo.hashPin(pin))
                    Toast.makeText(requireContext(), "PIN set successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "PIN must be at least 4 digits", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showClearAllDialog(prefs: ClonePreferences) {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Clear All Clones")
            .setMessage("This will permanently delete all cloned apps. Continue?")
            .setPositiveButton("Delete All") { _, _ ->
                prefs.getAllClones().forEach { prefs.deleteClone(it.id) }
                Toast.makeText(requireContext(), "All clones removed", Toast.LENGTH_SHORT).show()
                binding.tvTotalClones.text = "0 active clones"
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
