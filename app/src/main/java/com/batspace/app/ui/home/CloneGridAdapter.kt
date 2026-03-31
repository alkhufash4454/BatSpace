package com.batspace.app.ui.home

import android.content.pm.PackageManager
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.batspace.app.data.model.ClonedApp
import com.batspace.app.databinding.ItemCloneCardBinding

class CloneGridAdapter(
    private val onClick: (ClonedApp) -> Unit,
    private val onLongClick: (ClonedApp) -> Unit
) : ListAdapter<ClonedApp, CloneGridAdapter.VH>(DIFF) {

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<ClonedApp>() {
            override fun areItemsTheSame(a: ClonedApp, b: ClonedApp) = a.id == b.id
            override fun areContentsTheSame(a: ClonedApp, b: ClonedApp) = a == b
        }
    }

    inner class VH(val binding: ItemCloneCardBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemCloneCardBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val clone = getItem(position)
        val label = clone.customLabel.ifBlank { clone.displayName }
        holder.binding.tvCloneLabel.text = label
        holder.binding.tvInstance.text = "#${clone.instanceIndex}"
        holder.binding.ivLock.visibility = if (clone.isLocked) android.view.View.VISIBLE else android.view.View.GONE
        holder.binding.ivDisabled.visibility = if (!clone.isEnabled) android.view.View.VISIBLE else android.view.View.GONE

        try {
            val pm = holder.itemView.context.packageManager
            holder.binding.ivAppIcon.setImageDrawable(pm.getApplicationIcon(clone.originalPackage))
        } catch (e: PackageManager.NameNotFoundException) {
            holder.binding.ivAppIcon.setImageResource(android.R.drawable.sym_def_app_icon)
        }

        holder.itemView.setOnClickListener { onClick(clone) }
        holder.itemView.setOnLongClickListener { onLongClick(clone); true }
    }
}
