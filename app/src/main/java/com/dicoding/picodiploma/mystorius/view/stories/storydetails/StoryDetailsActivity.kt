package com.dicoding.picodiploma.mystorius.view.stories.storydetails

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.dicoding.picodiploma.mystorius.databinding.ActivityStoryDetailsBinding
import com.dicoding.picodiploma.mystorius.data.api.ListStoryItem

class StoryDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStoryDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val story = intent.getParcelableExtra<ListStoryItem>("story")
        story?.let {
            binding.titleTextView.text = it.name
            binding.descriptionTextView.text = it.description
            Glide.with(this)
                .load(it.photoUrl)
                .into(binding.imageView)
        }

        binding.imageView.transitionName = "storyImage"
        binding.titleTextView.transitionName = "storyTitle"
    }
}