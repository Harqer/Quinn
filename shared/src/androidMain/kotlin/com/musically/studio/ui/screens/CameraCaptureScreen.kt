package com.musically.studio.ui.screens

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.util.Base64
import timber.log.Timber
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.musically.studio.ui.utils.debouncedClickable
import androidx.core.content.ContextCompat
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import android.view.HapticFeedbackConstants
import androidx.compose.ui.platform.LocalView
import android.view.View

@Composable
fun CameraCaptureScreen(
    onImageCaptured: (String) -> Unit,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    var imageCapture: ImageCapture? by remember { mutableStateOf(null) }

    val view = LocalView.current

    val onCaptureClick: () -> Unit = click@{
        val capture = imageCapture ?: return@click
        view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
        val executor = ContextCompat.getMainExecutor(context)
        capture.takePicture(
            executor,
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: ImageProxy) {
                    val base64 = imageProxyToBase64(image)
                    image.close()
                    onImageCaptured(base64)
                }

                override fun onError(exception: ImageCaptureException) {
                    Timber.e(exception, "Photo capture failed: ${exception.message}")
                }
            }
        )
    }

    val factoryView: (Context) -> View = { ctx ->
        val previewView = PreviewView(ctx)
        val executor = ContextCompat.getMainExecutor(ctx)
        
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = androidx.camera.core.Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            val imageCaptureBuilder = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            
            imageCapture = imageCaptureBuilder.build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageCapture
                )
            } catch (exc: Exception) {
                Timber.e(exc, "Use case binding failed")
            }
        }, executor)
        previewView
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .debouncedClickable { onCaptureClick() }
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = factoryView
        )
    }
}

private fun imageProxyToBase64(image: ImageProxy): String {
    val planeProxy = image.planes[0]
    val buffer: ByteBuffer = planeProxy.buffer
    val bytes = ByteArray(buffer.remaining())
    buffer.get(bytes)
    
    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    val matrix = Matrix().apply {
        postRotate(image.imageInfo.rotationDegrees.toFloat())
    }
    val rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    
    val outputStream = ByteArrayOutputStream()
    rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
    val finalBytes = outputStream.toByteArray()
    
    return Base64.encodeToString(finalBytes, Base64.NO_WRAP)
}
