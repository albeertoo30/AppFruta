package com.appfruta

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import java.text.SimpleDateFormat
import java.util.Locale

class FruitItemAdapter(
    private val onEdit: (FruitItem) -> Unit
) : ListAdapter<FruitItem, FruitItemAdapter.ViewHolder>(DiffCallback) {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvFruitName)
        val tvExpiry: TextView = view.findViewById(R.id.tvFruitExpiry)
        val tvQuantity: TextView = view.findViewById(R.id.tvFruitQuantity)
        val chipStatus: Chip = view.findViewById(R.id.chipFruitStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_fruit, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        val ctx = holder.itemView.context
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        holder.tvName.text = item.name
        holder.tvQuantity.text = ctx.getString(R.string.quantity_format, item.quantity)
        holder.tvExpiry.text = ctx.getString(R.string.expiry_format, sdf.format(item.expiryDate.toDate()))

        val isFresh = item.label == "fresh"
        holder.chipStatus.text = ctx.getString(if (isFresh) R.string.state_fresh else R.string.state_rotten)
        holder.chipStatus.setChipBackgroundColorResource(if (isFresh) R.color.fruit_fresh_bg else R.color.fruit_rotten_bg)
        holder.chipStatus.setTextColor(ctx.getColor(if (isFresh) R.color.fruit_fresh else R.color.fruit_rotten))

        holder.itemView.setOnClickListener { onEdit(item) }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<FruitItem>() {
        override fun areItemsTheSame(old: FruitItem, new: FruitItem) = old.id == new.id
        override fun areContentsTheSame(old: FruitItem, new: FruitItem) = old == new
    }
}
