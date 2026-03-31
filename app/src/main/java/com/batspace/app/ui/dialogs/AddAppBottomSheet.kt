package com.batspace.app.ui.dialogs

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.batspace.app.data.model.InstalledApp
import com.batspace.app.databinding.BottomSheetAddAppBinding
import com.batspace.app.ui.home.HomeViewModel
import com.batspace.app.ui.home.InstalledAppAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AddAppBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetAddAppBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels({ requireParentFragment() })

    companion object {
        fun newInstance() = AddAppBottomSheet()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = BottomSheetAddAppBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val appAdapter = InstalledAppAdapter { app -> cloneApp(app) }
        binding.rvApps.layoutManager = LinearLayoutManager(requireContext())
        binding.rvApps.adapter = appAdapter

        viewModel.installedApps.observe(viewLifecycleOwner) { apps ->
            appAdapter.submitList(apps)
        }

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val filtered = viewModel.getFilteredApps(s?.toString() ?: "")
                appAdapter.submitList(filtered)
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun cloneApp(app: InstalledApp) {
        viewModel.addClone(app.packageName, app.appName)
        Toast.makeText(requireContext(), "\"${app.appName}\" cloned!", Toast.LENGTH_SHORT).show()
        dismiss()
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
