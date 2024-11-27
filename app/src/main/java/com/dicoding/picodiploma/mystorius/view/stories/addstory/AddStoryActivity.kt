package com.dicoding.picodiploma.mystorius.view.stories.addstory

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.picodiploma.mystorius.databinding.ActivityAddStoryBinding

class AddStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddStoryBinding
    private val viewModel: AddStoryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.currentImageUri.observe(this) { uri ->
            uri?.let { binding.previewImageView.setImageURI(it) }
        }

        viewModel.description.observe(this) { desc ->
            if (binding.descriptionEditText.text.toString() != desc) {
                binding.descriptionEditText.setText(desc)
            }
        }

        binding.galleryButton.setOnClickListener {
            startGallery()
        }

        binding.cameraButton.setOnClickListener {
            startCamera()
        }

        binding.submitButton.setOnClickListener {
            val description = binding.descriptionEditText.text.toString()
            viewModel.setDescription(description)
            uploadImage()
        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.setImageUri(uri)
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun startCamera() {
        val imageUri = getImageUri(this)
        viewModel.setImageUri(imageUri)
        launcherIntentCamera.launch(imageUri)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            viewModel.currentImageUri.value?.let {
                binding.previewImageView.setImageURI(it)
            }
        } else {
            viewModel.setImageUri(null)
        }
    }

    private fun uploadImage() {
        val description = viewModel.description.value
        val imageUri = viewModel.currentImageUri.value

        // TODO: Add Post Later
    }
}