# ===========================================
# Smart WebView ProGuard Rules
# ===========================================

# --- WebView & JavaScript Interface ---
# Keep the JS interface class and all its public methods
-keepclassmembers class mgks.os.swv.MainActivity$WebAppInterface {
    public *;
}
# Keep any JS interfaces added by plugins
-keepclassmembers class mgks.os.swv.plugins.** {
    public *;
}
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

# --- Plugin System ---
# Keep the plugin interface and all plugin implementations
-keep interface mgks.os.swv.PluginInterface { *; }
-keep class mgks.os.swv.plugins.** { *; }
-keep class mgks.os.swv.PluginManager { *; }
-keep class mgks.os.swv.SWVContext { *; }
-keep class mgks.os.swv.SWVContext$* { *; }
-keep class mgks.os.swv.Functions { *; }

# --- Firebase ---
-keep class com.google.firebase.** { *; }
-keep class com.google.firebase.messaging.** { *; }
-dontwarn com.google.firebase.**

# --- Google Play Services (Ads, Auth, Location) ---
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.android.gms.**
-keep public class com.google.android.gms.ads.** { public *; }
-dontwarn com.google.android.gms.ads.**

# --- ZXing (QR Scanner) ---
-keep class com.journeyapps.** { *; }
-keep class com.google.zxing.** { *; }
-dontwarn com.journeyapps.**
-dontwarn com.google.zxing.**

# --- AndroidX Biometric ---
-keep class androidx.biometric.** { *; }
-dontwarn androidx.biometric.**

# --- Keep generic types needed for reflection ---
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes EnclosingMethod
-keepattributes InnerClasses
-keepattributes SourceFile,LineNumberTable

# --- Suppress warnings ---
-dontwarn android.support.**
-dontwarn org.slf4j.**
