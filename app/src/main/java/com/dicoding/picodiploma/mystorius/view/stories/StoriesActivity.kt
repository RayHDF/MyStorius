package com.dicoding.picodiploma.mystorius.view.stories

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.picodiploma.mystorius.databinding.ActivityStoriesBinding
import com.dicoding.picodiploma.mystorius.data.pref.UserPreference
import com.dicoding.picodiploma.mystorius.data.pref.dataStore
import com.dicoding.picodiploma.mystorius.view.ViewModelFactory
import com.dicoding.picodiploma.mystorius.view.main.MainActivity
import com.dicoding.picodiploma.mystorius.view.stories.addstory.AddStoryActivity
import com.dicoding.picodiploma.mystorius.view.stories.maps.MapsActivity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class StoriesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStoriesBinding
    private val storiesViewModel: StoriesViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    private val adapter = StoriesPagingAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoriesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()

        val pref = UserPreference.getInstance(dataStore)
        val user = runBlocking { pref.getSession().first() }
        user.token.let {
            storiesViewModel.getStoriesPagingData(it).observe(this) { pagingData ->
                adapter.submitData(lifecycle, pagingData)
            }
        }

        binding.addStoryFab.setOnClickListener {
            val intent = Intent(this@StoriesActivity, AddStoryActivity::class.java)
            startActivity(intent)
        }

        binding.mapsFab.setOnClickListener {
            val intent = Intent(this@StoriesActivity, MapsActivity::class.java)
            startActivity(intent)
        }

        binding.logoutFab.setOnClickListener {
            runBlocking {
                pref.logout()
            }
            val intent = Intent(this@StoriesActivity, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }
}