package io.danielhartman.weedmaps.searchresults.resultadapter

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import io.danielhartman.weedmaps.R
import io.danielhartman.weedmaps.databinding.SearchResultItemBinding

data class SearchResultItem(val imageUrl: String, val name: String, val review: String) {
    class VH(private val binding: SearchResultItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: SearchResultItem) {
            binding.run {
                this.resultName.text = data.name
                Glide.with(binding.root.context).load(data.imageUrl).centerCrop().into(binding.resultImage)
                this.resultReview.text = data.review
            }
        }
    }

    companion object {
        val layout = R.layout.search_result_item
    }
}