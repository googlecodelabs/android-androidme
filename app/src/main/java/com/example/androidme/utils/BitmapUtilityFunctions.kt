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

package com.example.androidme.utils

import android.graphics.Bitmap
import android.graphics.Matrix
import androidx.exifinterface.media.ExifInterface


fun rotateImageToCorrectOrientation(bitmap: Bitmap, filePath: String): Bitmap {
    val ei = ExifInterface(filePath)

    // Detects orientation of phone
    val orientation = ei.getAttributeInt(
        ExifInterface.TAG_ORIENTATION,
        ExifInterface.ORIENTATION_UNDEFINED
    )
    val rotatedBitmap: Bitmap

    // Decides rotation of image based on phone orientation
    rotatedBitmap = when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> bitmap.rotateImage(90f)

        ExifInterface.ORIENTATION_ROTATE_180 -> bitmap.rotateImage(180f)

        ExifInterface.ORIENTATION_ROTATE_270 -> bitmap.rotateImage(270f)

        else -> bitmap
    }
    return rotatedBitmap
}

// Example of an extension function
// you can learn more about them here (https://kotlinlang.org/docs/reference/extensions.html)
fun Bitmap.rotateImage(angle: Float): Bitmap {
    val matrix = Matrix()
    matrix.postRotate(angle)
    return Bitmap.createBitmap(this, 0, 0, this.width, this.height, matrix, true)
}
