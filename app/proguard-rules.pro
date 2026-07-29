# Mave Production Proguard Rules

# Meta Wearables SDK - Refined public API & native bridging keep rules
-keep public class com.meta.wearable.dat.core.** { public *; }
-keep public class com.meta.wearable.dat.camera.** { public *; }
-keep public class com.meta.wearable.dat.display.** { public *; }

# Data Models for Serialization (Gson)
# We keep the names of these classes and their fields to ensure JSON mapping works.
-keepclassmembers class com.musically.studio.network.** {
    @com.google.gson.annotations.SerializedName <fields>;
}
-keep,allowobfuscation class com.musically.studio.network.**

# ViewModels and UI State (Shared Module)
# (Removed broad keep rule for UI package to resolve BroadKeepRule lint error)

# Interactions API Models (if any are added later to src/types)
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepattributes RuntimeVisibleTypeAnnotations, AnnotationDefault

# Spotify Auth SDK
-keep class com.spotify.sdk.android.auth.** { *; }
-keep interface com.spotify.sdk.android.auth.** { *; }

# Timber
-keep class timber.log.** { *; }
-dontwarn timber.log.**


