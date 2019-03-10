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

import android.app.Application
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.androidme.utils.rotateImageToCorrectOrientation
import createTempImageFile
import java.io.File

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val TAG = MainViewModel::class.java.name

    private val loadingImage = BitmapDrawable(
        application.resources,
        BitmapFactory.decodeResource(application.resources, R.drawable.android_you))

    private val startingImage = BitmapDrawable(
        application.resources,
        BitmapFactory.decodeResource(application.resources, R.drawable.android_me))

    private var tempPhotoFilePath: String? = null

    val androidImageDrawable: MutableLiveData<BitmapDrawable> = MutableLiveData()

    init {
        androidImageDrawable.value = startingImage
    }

    fun processAndSetImage() {
        androidImageDrawable.value = loadingImage

        tempPhotoFilePath?.let {
            // Rotates the photo from the camera
            val tempPhotoBitmap = rotateImageToCorrectOrientation(BitmapFactory.decodeFile(it), it)
            // Calls the facial detection on the correctly rotated image
            detectFaceAndAndroidIt(tempPhotoBitmap)
        }
    }

    fun createTempImageFile(): File? {
        val photoFile = createTempImageFile(getApplication<Application>())
        tempPhotoFilePath = photoFile?.absolutePath
        return photoFile
    }

    /** Face Detection and Drawing Code **/

    private fun detectFaceAndAndroidIt(photoBitmap: Bitmap){

        // 1. Create a new blank mutable Bitmap
        val resultBitmap = Bitmap.createBitmap(
            photoBitmap.width,
            photoBitmap.height,
            Bitmap.Config.RGB_565
        )

        // 2. Construct your Canvas using that Bitmap
        val canvas = Canvas(resultBitmap)

        // 3. Tell the Canvas to first draw the photo
        canvas.drawBitmap(photoBitmap, 0.toFloat(), 0.toFloat(), null)

        // 4. Tell the Canvas to draw some lines and shapes
        drawAntennaeAndEyes(canvas)

        // 5. Display the final tempAndroidFaceBitmap
        androidImageDrawable.value = BitmapDrawable(getApplication<Application>().resources, resultBitmap)

    }

    private fun drawAntennaeAndEyes(canvas: Canvas) {
        /** Configure and draw antennae **/

        // Configure paint settings
        val antennaePaint = Paint()
        antennaePaint.color = getApplication<Application>().getColor(R.color.android_green)
        antennaePaint.strokeWidth = 30f
        antennaePaint.strokeCap = Paint.Cap.ROUND

        // Set the length of the antennae
        val bottomAntennaeY = 400f
        val topAntennaeY = 350f

        // Draw left antennae
        canvas.drawLine(
            350f, topAntennaeY,
            360f, bottomAntennaeY, antennaePaint
        )

        // Draw right antennae
        canvas.drawLine(
            500f, topAntennaeY,
            490f, bottomAntennaeY, antennaePaint
        )
    }
}
