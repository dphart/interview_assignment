package io.danielhartman.weedmaps.searchresults.resultadapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import io.danielhartman.weedmaps.databinding.SearchResultItemBinding

class SearchResultAdapter(val onBottomReachedListener: (Int) -> Unit) :
    ListAdapter<SearchResultItem, SearchResultItem.VH>(
        Diff
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultItem.VH {
        val binding =
            SearchResultItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SearchResultItem.VH(
            binding
        )
    }

    override fun onBindViewHolder(holder: SearchResultItem.VH, position: Int) {
        holder.bind(getItem(position))
        if (position >= itemCount - 1) {
            onBottomReachedListener(itemCount)
        }
    }

    object Diff : DiffUtil.ItemCallback<SearchResultItem>() {
        override fun areItemsTheSame(
            oldItem: SearchResultItem,
            newItem: SearchResultItem
        ) = oldItem == newItem

        override fun areContentsTheSame(
            oldItem: SearchResultItem,
            newItem: SearchResultItem
        ) = oldItem == newItem

    }
}