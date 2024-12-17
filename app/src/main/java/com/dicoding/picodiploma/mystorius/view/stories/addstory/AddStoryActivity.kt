package com.dicoding.picodiploma.mystorius.view.stories.addstory

import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.dicoding.picodiploma.mystorius.R
import com.dicoding.picodiploma.mystorius.data.pref.UserPreference
import com.dicoding.picodiploma.mystorius.data.pref.dataStore
import com.dicoding.picodiploma.mystorius.databinding.ActivityAddStoryBinding
import com.dicoding.picodiploma.mystorius.view.ViewModelFactory
import com.dicoding.picodiploma.mystorius.view.stories.StoriesActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody


class AddStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddStoryBinding
    private val viewModel: AddStoryViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation { lat, lon ->
                    if (lat != null && lon != null) {
                        Log.d("Add Story", "$lat, $lon")
                    } else {
                        showToast("Unable to get current location")
                    }
                }
            }
        }

    private fun getMyLocation(callback: (Double?, Double?) -> Unit) {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    location?.let {
                        val lat = it.latitude
                        val lon = it.longitude
                        Log.d("Current Location", "$lat, $lon")
                        callback(lat, lon)
                    } ?: run {
                        showToast("Unable to get current location")
                        callback(null, null)
                    }
                }
                .addOnFailureListener {
                    showToast("Failed to get current location")
                    callback(null, null)
                }
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

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
            if (binding.switchEnableLocation.isChecked) {
                getMyLocation { lat, lon ->
                    if (lat != null && lon != null) {
                        uploadImage(lat, lon)
                    } else {
                        showToast("Unable to get current location")
                    }
                }
            } else {
                uploadImage(null, null)
            }
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

    private fun uploadImage(lat: Double?, lon: Double?) {
        viewModel.currentImageUri.value?.let { uri ->
            val imageFile = uriToFile(uri, this).reduceFileImage()
            Log.d("Image File", "showImage: ${imageFile.path}")

            val description = binding.descriptionEditText.text.toString()
            val descriptionRequestBody = description.toRequestBody("text/plain".toMediaType())
            val imageRequestBody = imageFile.asRequestBody("image/jpeg".toMediaType())
            val imageMultipart =
                MultipartBody.Part.createFormData("photo", imageFile.name, imageRequestBody)

            val latRequestBody = lat?.toString()?.toRequestBody("text/plain".toMediaType())
            val lonRequestBody = lon?.toString()?.toRequestBody("text/plain".toMediaType())

            val pref = UserPreference.getInstance(dataStore)
            val token = runBlocking { pref.getSession().first().token }

            Log.d("Bearer Token", "Bearer: $token")

            viewModel.uploadImage(
                token,
                imageMultipart,
                descriptionRequestBody,
                latRequestBody,
                lonRequestBody
            )
        } ?: showToast(getString(R.string.empty_image_warning))
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}