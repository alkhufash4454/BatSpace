package com.batspace.app.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.batspace.app.data.model.ClonedApp
import com.batspace.app.databinding.FragmentHomeBinding
import com.batspace.app.ui.clone.CloneDetailActivity
import com.batspace.app.ui.dialogs.AddAppBottomSheet

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val cloneAdapter = CloneGridAdapter(
            onClick = { clone -> openCloneDetail(clone) },
            onLongClick = { clone -> showCloneOptions(clone) }
        )
        binding.rvClones.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.rvClones.adapter = cloneAdapter

        viewModel.clones.observe(viewLifecycleOwner) { clones ->
            cloneAdapter.submitList(clones)
            binding.tvEmpty.visibility = if (clones.isEmpty()) View.VISIBLE else View.GONE
            binding.tvCloneCount.text = "${clones.size} clones"
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        }

        binding.fabAddApp.setOnClickListener {
            AddAppBottomSheet.newInstance().show(childFragmentManager, "add_app")
        }

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.loadAll()
            binding.swipeRefresh.isRefreshing = false
        }
    }

    private fun openCloneDetail(clone: ClonedApp) {
        val intent = Intent(requireContext(), CloneDetailActivity::class.java)
        intent.putExtra("clone_id", clone.id)
        startActivity(intent)
    }

    private fun showCloneOptions(clone: ClonedApp) {
        val label = clone.customLabel.ifBlank { clone.displayName }
        val options = arrayOf("✏️ Rename", "🔒 Lock / Unlock", "🔄 Refresh Identity", "🗑️ Delete Clone")
        AlertDialog.Builder(requireContext())
            .setTitle(label)
            .setItems(options) { _, which ->
                when (which) {
                    0 -> showRenameDialog(clone)
                    1 -> { viewModel.toggleLock(clone.id, !clone.isLocked); Toast.makeText(requireContext(), if (!clone.isLocked) "Locked" else "Unlocked", Toast.LENGTH_SHORT).show() }
                    2 -> { viewModel.refreshIdentity(clone.id); Toast.makeText(requireContext(), "Identity refreshed", Toast.LENGTH_SHORT).show() }
                    3 -> confirmDelete(clone)
                }
            }.show()
    }

    private fun showRenameDialog(clone: ClonedApp) {
        val input = android.widget.EditText(requireContext())
        input.setText(clone.customLabel.ifBlank { clone.displayName })
        AlertDialog.Builder(requireContext())
            .setTitle("Rename Clone")
            .setView(input)
            .setPositiveButton("Save") { _, _ -> viewModel.updateCloneLabel(clone.id, input.text.toString()) }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun confirmDelete(clone: ClonedApp) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Clone")
            .setMessage("Delete \"${clone.customLabel.ifBlank { clone.displayName }}\"?")
            .setPositiveButton("Delete") { _, _ -> viewModel.deleteClone(clone.id) }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onResume() { super.onResume(); viewModel.loadAll() }
    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
