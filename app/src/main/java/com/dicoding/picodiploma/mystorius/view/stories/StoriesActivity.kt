package com.dicoding.picodiploma.mystorius.view.stories

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.picodiploma.mystorius.databinding.ActivityStoriesBinding
import com.dicoding.picodiploma.mystorius.data.pref.UserPreference
import com.dicoding.picodiploma.mystorius.data.pref.dataStore
import com.dicoding.picodiploma.mystorius.view.stories.addstory.AddStoryActivity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class StoriesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStoriesBinding
    private val storiesViewModel: StoriesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoriesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        observeStories()

        val pref = UserPreference.getInstance(dataStore)
        val user = runBlocking { pref.getSession().first() }
        user.token?.let {
            storiesViewModel.fetchStories(it)
        }

        binding.fab.setOnClickListener {
            val intent = Intent(this@StoriesActivity, AddStoryActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun observeStories() {
        storiesViewModel.stories.observe(this) { stories ->
            binding.recyclerView.adapter = StoriesAdapter(stories)
        }
    }
}