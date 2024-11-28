package com.dicoding.picodiploma.mystorius.view.stories.addstory

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.dicoding.picodiploma.mystorius.R
import com.dicoding.picodiploma.mystorius.data.StoriesRepository
import com.dicoding.picodiploma.mystorius.data.api.ApiConfig
import com.dicoding.picodiploma.mystorius.data.api.FileUploadResponse
import com.dicoding.picodiploma.mystorius.data.pref.UserPreference
import com.dicoding.picodiploma.mystorius.data.pref.dataStore
import com.dicoding.picodiploma.mystorius.databinding.ActivityAddStoryBinding
import com.dicoding.picodiploma.mystorius.view.ViewModelFactory
import com.dicoding.picodiploma.mystorius.view.stories.StoriesActivity
import com.google.gson.Gson
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import retrofit2.Response

class AddStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddStoryBinding
    private val viewModel: AddStoryViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

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

        viewModel.uploadResult.observe(this) { response ->
            response?.let {
                Log.d("Upload Response", "Error: ${it.error}, Message: ${it.message}")
                if (it.error) {
                    showToast(it.message)
                } else {
                    showToast("Upload success")
                    val intent = Intent(this, StoriesActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                }
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
        viewModel.currentImageUri.value?.let { uri ->
            val imageFile = uriToFile(uri, this).reduceFileImage()
            Log.d("Image File", "showImage: ${imageFile.path}")

            val description = binding.descriptionEditText.text.toString()
            val descriptionRequestBody = description.toRequestBody("text/plain".toMediaType())
            val imageRequestBody = imageFile.asRequestBody("image/jpeg".toMediaType())
            val imageMultipart = MultipartBody.Part.createFormData("photo", imageFile.name, imageRequestBody)

            val pref = UserPreference.getInstance(dataStore)
            val token = runBlocking { pref.getSession().first().token }

            Log.d("Bearer Token", "Bearer: $token")

            viewModel.uploadImage(token, imageMultipart, descriptionRequestBody)
        } ?: showToast(getString(R.string.empty_image_warning))
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}