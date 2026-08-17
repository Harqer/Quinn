/**
 * @AtomicLevel: Organism
 * @SemanticPurpose: Android Component for CameraPreview.kt
 */

package com.musically.studio.ui.components

import com.musically.studio.ui.*

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.*
import android.util.Base64
import android.util.Size
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.compose.CameraXViewfinder
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.musically.studio.ui.MainViewModel
import java.io.ByteArrayOutputStream
import java.util.concurrent.Executors
import timber.log.Timber

@Composable
fun CameraPreview(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel? = null
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    
    var surfaceRequest by remember { mutableStateOf<SurfaceRequest?>(null) }
    val preview = remember { 
        Preview.Builder().build().apply {
            setSurfaceProvider { request -> surfaceRequest = request }
        }
    }
    val imageAnalysis = remember {
        ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
    }

    var lastAnalysisTime by remember { mutableLongStateOf(0L) }

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted -> hasCameraPermission = isGranted }
    )

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    if (!hasCameraPermission) {
        Column(
            modifier = modifier.fillMaxSize().padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Camera access is required to analyze your surroundings and generate ambient music.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) }) {
                Text("Grant Camera Permission")
            }
        }
        return
    }

    LaunchedEffect(context, lifecycleOwner) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
                val currentTime = System.currentTimeMillis()
                // Throttled analysis: 1 frame per 1.5 seconds for production stability
                if (currentTime - lastAnalysisTime >= 1500) {
                    val base64 = imageProxy.toBase64()
                    if (base64 != null) {
                        viewModel?.sendFrame(base64)
                    }
                    lastAnalysisTime = currentTime
                }
                imageProxy.close()
            }

            val extensionsManagerFuture = androidx.camera.extensions.ExtensionsManager.getInstanceAsync(context, cameraProvider)
            extensionsManagerFuture.addListener({
                val extensionsManager = extensionsManagerFuture.get()
                var cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                if (extensionsManager.isExtensionAvailable(cameraSelector, androidx.camera.extensions.ExtensionMode.AUTO)) {
                    cameraSelector = extensionsManager.getExtensionEnabledCameraSelector(cameraSelector, androidx.camera.extensions.ExtensionMode.AUTO)
                } else if (extensionsManager.isExtensionAvailable(cameraSelector, androidx.camera.extensions.ExtensionMode.NIGHT)) {
                    cameraSelector = extensionsManager.getExtensionEnabledCameraSelector(cameraSelector, androidx.camera.extensions.ExtensionMode.NIGHT)
                } else if (extensionsManager.isExtensionAvailable(cameraSelector, androidx.camera.extensions.ExtensionMode.HDR)) {
                    cameraSelector = extensionsManager.getExtensionEnabledCameraSelector(cameraSelector, androidx.camera.extensions.ExtensionMode.HDR)
                }

                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        imageAnalysis
                    )
                } catch (e: Exception) {
                    Timber.e(e, "Use case binding failed")
                }
            }, ContextCompat.getMainExecutor(context))
        }, ContextCompat.getMainExecutor(context))
    }

    surfaceRequest?.let { request ->
        CameraXViewfinder(
            surfaceRequest = request,
            modifier = modifier.fillMaxSize()
        )
    }
}

/**
 * Extension to convert ImageProxy (YUV_420_888) to Base64 JPEG string.
 */
private fun ImageProxy.toBase64(): String? {
    val yBuffer = planes[0].buffer
    val uBuffer = planes[1].buffer
    val vBuffer = planes[2].buffer

    val ySize = yBuffer.remaining()
    val uSize = uBuffer.remaining()
    val vSize = vBuffer.remaining()

    val nv21 = ByteArray(ySize + uSize + vSize)

    // U and V are swapped
    yBuffer.get(nv21, 0, ySize)
    vBuffer.get(nv21, ySize, vSize)
    uBuffer.get(nv21, ySize + vSize, uSize)

    val yuvImage = YuvImage(nv21, ImageFormat.NV21, width, height, null)
    val out = ByteArrayOutputStream()
    yuvImage.compressToJpeg(Rect(0, 0, width, height), 70, out)
    val imageBytes = out.toByteArray()
    return Base64.encodeToString(imageBytes, Base64.NO_WRAP)
}
