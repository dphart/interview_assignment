package io.danielhartman.weedmaps.searchscreen.recentadapter

import androidx.recyclerview.widget.RecyclerView
import io.danielhartman.weedmaps.databinding.RecentSearchItemBinding

data class RecentSearchItem(val recentSearch: String) {
    class VH(private val binding: RecentSearchItemBinding, val clickListener: (String) -> Unit) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: String) {
            binding.recentSearchText.run {
                text = data
                setOnClickListener { clickListener(data) }
            }
        }
    }

}