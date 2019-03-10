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
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.androidme.utils.rotateImageToCorrectOrientation
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.face.FirebaseVisionFace
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions
import com.google.firebase.ml.vision.face.FirebaseVisionFaceLandmark
import createTempImageFile
import java.io.File

private const val EYE_TO_FACE_RATIO = 12
private const val ANTENNAE_HEIGHT_RATIO = 4
private const val ANTENNAE_WIDTH_RATIO = 15

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

    private var firebaseFaceDetector: FirebaseVisionFaceDetector

    init {
        androidImageDrawable.value = startingImage

        // Sets options for facial detection
        val firebaseFaceDetectionOptions = FirebaseVisionFaceDetectorOptions.Builder()
            // Sets the option to favor accuracy over speed
            .setPerformanceMode(FirebaseVisionFaceDetectorOptions.ACCURATE)
            // Sets the detector to find all facial landmarks
            .setLandmarkMode(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
            // Allows us to tell if eyes are open or face is smiling
            .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
            .build()

        firebaseFaceDetector = FirebaseVision.getInstance()
            .getVisionFaceDetector(firebaseFaceDetectionOptions)
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


        val firebaseVisionImage = FirebaseVisionImage.fromBitmap(photoBitmap)

        // Run the facial detection
        firebaseFaceDetector.detectInImage(firebaseVisionImage)
            .addOnSuccessListener { faces ->

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

                // For each face in faces, draw the android ears and eyes
                for (face in faces) {
                    // 4. Tell the Canvas to draw some lines and shapes
                    drawAntennaeAndEyes(face, canvas)
                }

                // 5. Display the final tempAndroidFaceBitmap
                androidImageDrawable.value = BitmapDrawable(getApplication<MyApplication>().resources, resultBitmap)

            }
            .addOnFailureListener {
                Log.i(TAG, "There are no faces detected in this photo, try again")
            }

    }

    private fun drawAntennaeAndEyes(firebaseFace: FirebaseVisionFace, tempAndroidFaceCanvas: Canvas) {

        // Locate eyes on face and save into variables
        val leftEye = firebaseFace.getLandmark(FirebaseVisionFaceLandmark.LEFT_EYE)
        val rightEye = firebaseFace.getLandmark(FirebaseVisionFaceLandmark.RIGHT_EYE)

        if (leftEye == null || rightEye == null) {
            return
        }

        /** Configure and draw antennae **/

        // Configure paint settings
        val antennaePaint = Paint()
        antennaePaint.color = getApplication<MyApplication>().getColor(R.color.android_green)
        antennaePaint.strokeWidth = firebaseFace.boundingBox.width() / ANTENNAE_WIDTH_RATIO.toFloat()
        antennaePaint.strokeCap = Paint.Cap.ROUND

        // Set the length of the antennae
        val bottomAntennaeY = firebaseFace.boundingBox.top.toFloat()
        val topAntennaeY = (bottomAntennaeY - firebaseFace.boundingBox.height() / ANTENNAE_HEIGHT_RATIO)

        // Draw left antennae
        tempAndroidFaceCanvas.drawLine(
            firebaseFace.boundingBox.left.toFloat(), topAntennaeY,
            leftEye.position.x, bottomAntennaeY, antennaePaint
        )

        // Draw right antennae
        tempAndroidFaceCanvas.drawLine(
            firebaseFace.boundingBox.right.toFloat(), topAntennaeY,
            rightEye.position.x, bottomAntennaeY, antennaePaint
        )

        /** Configure and draw eyes **/

        // Configure eye settings
        val eyePaint = Paint()
        eyePaint.color = Color.WHITE

        // Set radius of eyes
        val eyeRadius: Float = (firebaseFace.boundingBox.width() / EYE_TO_FACE_RATIO).toFloat()

        // Draw left eye
        tempAndroidFaceCanvas.drawCircle(leftEye.position.x, leftEye.position.y, eyeRadius, eyePaint)
        // Draw right eye
        tempAndroidFaceCanvas.drawCircle(rightEye.position.x, rightEye.position.y, eyeRadius, eyePaint)
    }
}
