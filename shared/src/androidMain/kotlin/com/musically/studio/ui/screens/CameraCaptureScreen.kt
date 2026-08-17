/**
 * @AtomicLevel: Template/Page
 * @SemanticPurpose: Android Component for CameraCaptureScreen.kt
 */

package com.musically.studio.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.hardware.camera2.CameraMetadata
import android.os.Build
import android.os.PowerManager
import android.util.Base64
import android.view.HapticFeedbackConstants
import android.view.Surface
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.camera2.interop.Camera2Interop
import androidx.camera.compose.CameraXViewfinder
import androidx.camera.core.*
import androidx.camera.extensions.ExtensionMode
import androidx.camera.extensions.ExtensionsManager
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.*
import androidx.camera.video.VideoCapture
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.musically.studio.ui.components.atoms.CameraShutterButton
import com.musically.studio.ui.components.molecules.*
import com.musically.studio.ui.components.organisms.CameraTopToolbar
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.io.File
import java.nio.ByteBuffer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraCaptureScreen(
    onImageCaptured: (String) -> Unit,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val view = LocalView.current

    var hasCameraPermission by remember {
        mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
    }
    var hasAudioPermission by remember {
        mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasCameraPermission = permissions[Manifest.permission.CAMERA] ?: hasCameraPermission
        hasAudioPermission = permissions[Manifest.permission.RECORD_AUDIO] ?: hasAudioPermission
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission || !hasAudioPermission) {
            permissionLauncher.launch(arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO))
        }
    }

    if (!hasCameraPermission) {
        Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background), contentAlignment = Alignment.Center) {
            Text("Camera permission is required.", color = Color.White)
        }
        return
    }

    var captureMode by remember { mutableStateOf<CameraCaptureMode>(CameraCaptureMode.PHOTO) }
    var lensFacing by remember { mutableIntStateOf(CameraSelector.LENS_FACING_BACK) }
    var flashMode by remember { mutableIntStateOf(ImageCapture.FLASH_MODE_OFF) }
    var isTorchEnabled by remember { mutableStateOf(false) }
    var showGridLines by remember { mutableStateOf(true) }
    var currentAspect by remember { mutableStateOf<CameraAspect>(CameraAspect.RATIO_16_9) }
    var selectedExtensionMode by remember { mutableStateOf<ActiveExtensionMode>(ActiveExtensionMode.AUTO) }
    var isLowLightBoostEnabled by remember { mutableStateOf(false) }
    var exposureCompensationIndex by remember { mutableIntStateOf(0) }
    var showExposureSlider by remember { mutableStateOf(false) }

    var zoomLinearRatio by remember { mutableFloatStateOf(0f) }
    var tapFocusPoint by remember { mutableStateOf<Offset?>(null) }
    var activeCamera by remember { mutableStateOf<Camera?>(null) }
    var isCapturing by remember { mutableStateOf(false) }
    var isRecordingVideo by remember { mutableStateOf(false) }
    var activeRecording by remember { mutableStateOf<Recording?>(null) }

    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    var imageCapture: ImageCapture? by remember { mutableStateOf(null) }
    var videoCapture: VideoCapture<Recorder>? by remember { mutableStateOf(null) }
    var surfaceRequest by remember { mutableStateOf<SurfaceRequest?>(null) }

    val preview = remember(currentAspect) {
        val builder = Preview.Builder().setTargetAspectRatio(currentAspect.aspectRatioValue)
        Camera2Interop.Extender(builder).setStreamUseCase(
            if (captureMode == CameraCaptureMode.VIDEO) {
                CameraMetadata.SCALER_AVAILABLE_STREAM_USE_CASES_VIDEO_RECORD.toLong()
            } else {
                CameraMetadata.SCALER_AVAILABLE_STREAM_USE_CASES_PREVIEW.toLong()
            }
        )
        builder.build().apply {
            setSurfaceProvider { request -> surfaceRequest = request }
        }
    }

    LaunchedEffect(tapFocusPoint) {
        if (tapFocusPoint != null) {
            kotlinx.coroutines.delay(1500)
            tapFocusPoint = null
        }
    }

    val onCapturePhotoClick: () -> Unit = click@{
        val capture = imageCapture ?: return@click
        if (isCapturing) return@click
        isCapturing = true
        view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)

        capture.takePicture(
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: ImageProxy) {
                    val base64 = imageProxyToBase64(image, lensFacing == CameraSelector.LENS_FACING_FRONT)
                    image.close()
                    isCapturing = false
                    onImageCaptured(base64)
                }

                override fun onError(exception: ImageCaptureException) {
                    isCapturing = false
                    Timber.e(exception, "Photo capture failed")
                }
            }
        )
    }

    val onToggleVideoRecording: () -> Unit = {
        if (isRecordingVideo) {
            activeRecording?.stop()
            activeRecording = null
            isRecordingVideo = false
        } else {
            val vc = videoCapture
            if (vc != null) {
                val outputFile = File(context.cacheDir, "video_${System.currentTimeMillis()}.mp4")
                var pendingRecording = vc.output.prepareRecording(context, FileOutputOptions.Builder(outputFile).build())
                if (hasAudioPermission) pendingRecording = pendingRecording.withAudioEnabled()

                isRecordingVideo = true
                view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                activeRecording = pendingRecording.start(ContextCompat.getMainExecutor(context)) { event ->
                    if (event is VideoRecordEvent.Finalize) isRecordingVideo = false
                }
            }
        }
    }

    LaunchedEffect(lensFacing, currentAspect, selectedExtensionMode, flashMode, captureMode) {
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val rotation = view.display?.rotation ?: Surface.ROTATION_0

            val newImageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .setTargetAspectRatio(currentAspect.aspectRatioValue)
                .setFlashMode(flashMode)
                .setTargetRotation(rotation)
                .build()
            imageCapture = newImageCapture

            val recorder = Recorder.Builder().setQualitySelector(QualitySelector.from(Quality.HIGHEST)).build()
            val newVideoCapture = VideoCapture.withOutput(recorder)
            videoCapture = newVideoCapture

            val extensionsManagerFuture = ExtensionsManager.getInstanceAsync(context, cameraProvider)
            extensionsManagerFuture.addListener({
                val extensionsManager = extensionsManagerFuture.get()
                var baseSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()
                if (extensionsManager.isExtensionAvailable(baseSelector, selectedExtensionMode.mode)) {
                    baseSelector = extensionsManager.getExtensionEnabledCameraSelector(baseSelector, selectedExtensionMode.mode)
                }

                try {
                    cameraProvider.unbindAll()
                    val boundCamera = if (captureMode == CameraCaptureMode.VIDEO) {
                        cameraProvider.bindToLifecycle(lifecycleOwner, baseSelector, preview, newVideoCapture)
                    } else {
                        cameraProvider.bindToLifecycle(lifecycleOwner, baseSelector, preview, newImageCapture)
                    }
                    activeCamera = boundCamera
                    boundCamera.cameraControl.enableTorch(isTorchEnabled)
                    boundCamera.cameraControl.setLinearZoom(zoomLinearRatio)
                    boundCamera.cameraControl.setExposureCompensationIndex(exposureCompensationIndex)
                } catch (exc: Exception) {
                    Timber.e(exc, "CameraX bind error")
                }
            }, ContextCompat.getMainExecutor(context))
        }, ContextCompat.getMainExecutor(context))
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        surfaceRequest?.let { request ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTransformGestures { _, _, zoom, _ ->
                            val cam = activeCamera ?: return@detectTransformGestures
                            val newZoom = (zoomLinearRatio + (zoom - 1f) * 0.5f).coerceIn(0f, 1f)
                            zoomLinearRatio = newZoom
                            cam.cameraControl.setLinearZoom(newZoom)
                        }
                    }
                    .pointerInput(Unit) {
                        detectTapGestures { offset ->
                            tapFocusPoint = offset
                            val cam = activeCamera ?: return@detectTapGestures
                            val factory = SurfaceOrientedMeteringPointFactory(size.width.toFloat(), size.height.toFloat())
                            val action = FocusMeteringAction.Builder(factory.createPoint(offset.x, offset.y), FocusMeteringAction.FLAG_AF or FocusMeteringAction.FLAG_AE).build()
                            cam.cameraControl.startFocusAndMetering(action)
                        }
                    }
            ) {
                CameraXViewfinder(surfaceRequest = request, modifier = Modifier.fillMaxSize())
                if (showGridLines) CameraGridOverlay()
                tapFocusPoint?.let { pos -> CameraFocusRing(tapPoint = pos) }
            }
        }

        CameraTopToolbar(
            lensFacing = lensFacing,
            isLowLightBoostEnabled = isLowLightBoostEnabled,
            isTorchEnabled = isTorchEnabled,
            flashMode = flashMode,
            showExposureSlider = showExposureSlider,
            showGridLines = showGridLines,
            currentAspect = currentAspect,
            onClose = onClose,
            onToggleLowLightBoost = {
                isLowLightBoostEnabled = !isLowLightBoostEnabled
                activeCamera?.cameraControl?.enableLowLightBoostAsync(isLowLightBoostEnabled)
            },
            onToggleTorch = {
                isTorchEnabled = !isTorchEnabled
                activeCamera?.cameraControl?.enableTorch(isTorchEnabled)
            },
            onCycleFlashMode = {
                flashMode = when (flashMode) {
                    ImageCapture.FLASH_MODE_OFF -> ImageCapture.FLASH_MODE_ON
                    ImageCapture.FLASH_MODE_ON -> ImageCapture.FLASH_MODE_AUTO
                    else -> ImageCapture.FLASH_MODE_OFF
                }
            },
            onToggleExposureSlider = { showExposureSlider = !showExposureSlider },
            onToggleGridLines = { showGridLines = !showGridLines },
            onCycleAspectRatio = {
                currentAspect = when (currentAspect) {
                    CameraAspect.RATIO_16_9 -> CameraAspect.RATIO_4_3
                    CameraAspect.RATIO_4_3 -> CameraAspect.RATIO_1_1
                    CameraAspect.RATIO_1_1 -> CameraAspect.RATIO_16_9
                }
            }
        )

        CameraModeBar(
            currentMode = captureMode,
            onModeSelected = { captureMode = it },
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 190.dp)
        )

        CameraExtensionBar(
            selectedExtensionMode = selectedExtensionMode,
            onExtensionSelected = { selectedExtensionMode = it },
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 150.dp)
        )

        CameraZoomBar(
            currentLinearZoom = zoomLinearRatio,
            onZoomSelected = {
                zoomLinearRatio = it
                activeCamera?.cameraControl?.setLinearZoom(it)
            },
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 110.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter).navigationBarsPadding().padding(bottom = 24.dp, start = 32.dp, end = 32.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(48.dp))

            CameraShutterButton(
                captureMode = captureMode,
                isCapturing = isCapturing,
                isRecordingVideo = isRecordingVideo,
                onClick = { if (captureMode == CameraCaptureMode.PHOTO) onCapturePhotoClick() else onToggleVideoRecording() }
            )

            IconButton(
                onClick = {
                    lensFacing = if (lensFacing == CameraSelector.LENS_FACING_BACK) CameraSelector.LENS_FACING_FRONT else CameraSelector.LENS_FACING_BACK
                    view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                },
                modifier = Modifier
                    .size(48.dp)
                    .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                    .semantics {
                        contentDescription = if (lensFacing == CameraSelector.LENS_FACING_FRONT) "Switch to Rear Camera" else "Switch to Front Selfie Camera"
                    }
            ) {
                Icon(Icons.Default.Cameraswitch, contentDescription = null, tint = Color.White)
            }
        }

        // Screen Flash & Shutter Effect (Screen flash overlay for front camera / physical shutter effect)
        if (isCapturing) {
            val flashColor = if (lensFacing == CameraSelector.LENS_FACING_FRONT && flashMode != ImageCapture.FLASH_MODE_OFF) {
                Color.White.copy(alpha = 0.98f)
            } else {
                Color.White.copy(alpha = 0.85f)
            }
            Box(modifier = Modifier.fillMaxSize().background(flashColor))
        }
    }
}

private fun imageProxyToBase64(image: ImageProxy, isFrontFacing: Boolean = false): String {
    val buffer: ByteBuffer = image.planes[0].buffer
    val bytes = ByteArray(buffer.remaining())
    buffer.get(bytes)
    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    val matrix = Matrix().apply {
        postRotate(image.imageInfo.rotationDegrees.toFloat())
        if (isFrontFacing) postScale(-1f, 1f)
    }
    val rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    val outputStream = ByteArrayOutputStream()
    rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
    return Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
}
