# Musically Production Proguard Rules

# Meta Wearables SDK - Keep core classes required for native bridging
-keep class com.meta.wearable.** { *; }

# Data Models for Serialization (Gson)
# We keep the names of these classes and their fields to ensure JSON mapping works.
-keepclassmembers class com.musically.studio.network.** {
    @com.google.gson.annotations.SerializedName <fields>;
}
-keep class com.musically.studio.network.MaveArtist
-keep class com.musically.studio.network.MaveImage
-keep class com.musically.studio.network.MaveAlbum
-keep class com.musically.studio.network.MaveTrack
-keep class com.musically.studio.network.MaveTrackItem
-keep class com.musically.studio.network.MaveTracksResponse

# Interactions API Models (if any are added later to src/types)
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepattributes RuntimeVisibleTypeAnnotations, AnnotationDefault

# General optimizations
-repackageclasses ''
-allowaccessmodification
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
