package io.danielhartman.weedmaps.searchscreen.recentadapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.danielhartman.weedmaps.databinding.RecentSearchItemBinding

class RecentSearchAdapter(private val clickListener:(String)-> Unit) : RecyclerView.Adapter<RecentSearchItem.VH>() {
    var data:List<RecentSearchItem> = listOf()
    set(value) {
        field = value
        //We could make this smarter with Diff Utils but right now no data will be changing while it's being displayed
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentSearchItem.VH {
        val binding = RecentSearchItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecentSearchItem.VH(
            binding,
            clickListener
        )
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: RecentSearchItem.VH, position: Int) {
        holder.bind(data[position].recentSearch)
    }
}