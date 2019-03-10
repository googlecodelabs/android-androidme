/*
 * Copyright 2019, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.androidme

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders

private const val REQUEST_IMAGE_CAPTURE = 2

class MainActivity : AppCompatActivity() {

    private lateinit var androidMeImageView: ImageView
    private lateinit var photoButton: Button

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        androidMeImageView = findViewById(R.id.androidImage)
        photoButton = findViewById(R.id.androidButton)

        // Gets the ViewModel associated with "this" Activity
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        // Set onClickListener
        photoButton.setOnClickListener {
            // Launch the camera
            val intent = cameraIntent()
            if (intent != null) {
                ActivityCompat.startActivityForResult(
                    this,
                    intent,
                    REQUEST_IMAGE_CAPTURE,
                    null)
            }
        }

        // Update the layout when finalPhotoBitmapDrawable changes
        viewModel.androidImageDrawable.observe(this, Observer { bitmapDrawable ->
            // Update the layout
            androidMeImageView.setImageDrawable(bitmapDrawable)
        })

    }

    private fun cameraIntent(): Intent? {
        // Create the capture image intent
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            return takePictureIntent
        }
        return null
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        // If the image capture activity was called and was successful
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            // Process the image and set it to the TextView
            viewModel.processAndSetImage()
        }
    }
}
