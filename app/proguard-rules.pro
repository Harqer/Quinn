# Musically Production Proguard Rules

# Meta Wearables SDK
-keep class com.meta.wearable.** { *; }

# CameraX
-keep class androidx.camera.** { *; }

# Firebase
-keep class com.google.firebase.** { *; }

# Retrofit & OkHttp
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepattributes RuntimeVisibleTypeAnnotations, AnnotationDefault
-keep class retrofit2.** { *; }
-keep class okhttp3.** { *; }
-dontwarn retrofit2.**
-dontwarn okhttp3.**

# Gson
-keep class com.google.gson.** { *; }
-keepattributes *Annotation*
-keep class com.musically.studio.network.models.** { *; }

# Timber
-keep class timber.log.** { *; }

# LangChain / Serialization (if used in Android, currently mainly backend)
-keep class kotlinx.serialization.** { *; }

# General optimizations
-repackageclasses ''
-allowaccessmodification
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
