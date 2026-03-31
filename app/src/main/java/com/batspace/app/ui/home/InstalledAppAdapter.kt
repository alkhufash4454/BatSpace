package com.batspace.app.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.batspace.app.data.model.InstalledApp
import com.batspace.app.databinding.ItemInstalledAppBinding

class InstalledAppAdapter(
    private val onClick: (InstalledApp) -> Unit
) : ListAdapter<InstalledApp, InstalledAppAdapter.VH>(DIFF) {

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<InstalledApp>() {
            override fun areItemsTheSame(a: InstalledApp, b: InstalledApp) = a.packageName == b.packageName
            override fun areContentsTheSame(a: InstalledApp, b: InstalledApp) = a == b
        }
    }

    inner class VH(val binding: ItemInstalledAppBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemInstalledAppBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val app = getItem(position)
        holder.binding.tvAppName.text = app.appName
        holder.binding.tvPackage.text = app.packageName
        holder.binding.ivAppIcon.setImageDrawable(app.icon)
        holder.binding.tvCloneCount.text = if (app.cloneCount > 0) "${app.cloneCount} clones" else "Tap to clone"
        holder.itemView.setOnClickListener { onClick(app) }
    }
}
