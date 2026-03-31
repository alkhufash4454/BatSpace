package com.batspace.app.ui.clone

import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.batspace.app.databinding.ActivityCloneDetailBinding
import com.batspace.app.ui.home.HomeViewModel

class CloneDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCloneDetailBinding
    private lateinit var viewModel: HomeViewModel
    private var cloneId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCloneDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        cloneId = intent.getStringExtra("clone_id") ?: run { finish(); return }

        viewModel.clones.observe(this) { clones ->
            val clone = clones.find { it.id == cloneId } ?: return@observe
            val label = clone.customLabel.ifBlank { clone.displayName }
            binding.tvCloneName.text = label
            binding.tvOriginalPackage.text = clone.originalPackage
            binding.tvInstance.text = "Instance #${clone.instanceIndex}"
            binding.tvAndroidId.text = clone.virtualAndroidId.ifBlank { "N/A" }
            binding.tvDeviceId.text = clone.virtualDeviceId.ifBlank { "N/A" }
            binding.switchLock.isChecked = clone.isLocked
            binding.switchNotifications.isChecked = clone.notificationsEnabled
            try {
                binding.ivAppIcon.setImageDrawable(packageManager.getApplicationIcon(clone.originalPackage))
            } catch (e: PackageManager.NameNotFoundException) {
                binding.ivAppIcon.setImageResource(android.R.drawable.sym_def_app_icon)
            }
        }

        binding.btnBack.setOnClickListener { finish() }

        binding.btnRefreshIdentity.setOnClickListener {
            viewModel.refreshIdentity(cloneId)
            Toast.makeText(this, "Virtual identity regenerated", Toast.LENGTH_SHORT).show()
        }

        binding.switchLock.setOnCheckedChangeListener { _, checked ->
            viewModel.toggleLock(cloneId, checked)
        }

        binding.btnDelete.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Delete Clone")
                .setMessage("This will permanently remove the clone.")
                .setPositiveButton("Delete") { _, _ ->
                    viewModel.deleteClone(cloneId)
                    finish()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        viewModel.loadAll()
    }
}
