# Keep native methods
-keep class com.demoncore.soultracker.core.CriticalityEngine { *; }

# Preserve Rust JNI symbols
-keepclasseswithmembers class * {
    native <methods>;
}

# Never obfuscate criticality constants
-keepclassmembers class com.demoncore.soultracker.core.CriticalityConstants {
    static final *;
}

# Keep SIWE verifier calls
-keep class * implements com.demoncore.soultracker.crypto.SIWEVerifier { *; }

# Anti-reflection / anti-serialization
-dontobfuscate
-dontoptimize
