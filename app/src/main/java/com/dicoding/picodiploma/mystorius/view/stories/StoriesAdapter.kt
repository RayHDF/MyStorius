package com.dicoding.picodiploma.mystorius.view.stories

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.picodiploma.mystorius.databinding.ItemStoryBinding
import com.dicoding.picodiploma.mystorius.data.api.ListStoryItem

class StoriesAdapter(private val stories: List<ListStoryItem>) : RecyclerView.Adapter<StoriesAdapter.StoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        holder.bind(stories[position])
    }

    override fun getItemCount(): Int = stories.size

    class StoryViewHolder(private val binding: ItemStoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(story: ListStoryItem) {
            binding.titleTextView.text = story.name
            binding.descriptionTextView.text = story.description
            Glide.with(binding.imageView.context)
                .load(story.photoUrl)
                .into(binding.imageView)
        }
    }
}