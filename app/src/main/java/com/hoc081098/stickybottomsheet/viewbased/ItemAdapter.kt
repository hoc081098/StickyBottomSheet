package com.hoc081098.stickybottomsheet.viewbased

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hoc081098.stickybottomsheet.viewbased.ItemAdapter.MyViewHolder
import com.hoc081098.stickybottomsheet.databinding.ItemLayoutBinding

class ItemAdapter(private val strings: List<String>) : RecyclerView.Adapter<MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = MyViewHolder(
        ItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.binding.itemTextview.text = strings[position]
    }

    override fun getItemCount() = strings.size

    class MyViewHolder(internal val binding: ItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)
}