# Musically Production Proguard Rules

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

# Interactions API Models (if any are added later to src/types)
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepattributes RuntimeVisibleTypeAnnotations, AnnotationDefault


