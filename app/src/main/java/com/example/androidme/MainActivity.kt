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

import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
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

        androidMeImageView.setImageDrawable(viewModel.androidImageDrawable)

        // Set onClickListener
        photoButton.setOnClickListener {
            viewModel.processAndSetImage()
            androidMeImageView.setImageDrawable(viewModel.androidImageDrawable)
        }
    }
}
