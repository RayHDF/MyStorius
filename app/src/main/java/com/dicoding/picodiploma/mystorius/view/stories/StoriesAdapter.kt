package com.dicoding.picodiploma.mystorius.view.stories

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.picodiploma.mystorius.databinding.ItemStoryBinding
import com.dicoding.picodiploma.mystorius.data.api.ListStoryItem
import com.dicoding.picodiploma.mystorius.view.stories.storydetails.StoryDetailsActivity

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

            binding.root.setOnClickListener {
                val context = binding.root.context
                val intent = Intent(context, StoryDetailsActivity::class.java)
                intent.putExtra("story", story)

                val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    context as Activity,
                    Pair(binding.imageView, "storyImage"),
                    Pair(binding.titleTextView, "storyTitle")
                )
                context.startActivity(intent, options.toBundle())
            }
        }
    }
}