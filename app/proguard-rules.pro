
# General Options
-verbose
-dontpreverify

-obfuscationdictionary keywords.txt

# Suppress duplicate warning for system classes;  Blaze is passing android.jar
# to proguard multiple times.
-dontnote android.**
-dontnote dalvik.**
-dontnote com.android.**
-dontnote google.**
-dontnote com.google.**
-dontnote java.**
-dontnote javax.**
-dontnote junit.**
-dontnote org.apache.**
-dontnote org.json.**
-dontnote org.w3c.dom.**
-dontnote org.xml.sax.**
-dontnote org.xmlpull.v1.**

# Stop warnings about missing unused classes
-dontwarn android.**
-dontwarn dalvik.**
-dontwarn com.android.**
-dontwarn google.**
-dontwarn com.google.**
-dontwarn java.**
-dontwarn javax.**
-dontwarn junit.**
-dontwarn org.apache.**
-dontwarn org.json.**
-dontwarn org.w3c.dom.**
-dontwarn org.xml.sax.**
-dontwarn org.xmlpull.v1.**

# Input/Output Options
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers

# In the optimization step, ProGuard will then remove calls to such methods, if it can determine that the return values aren't used.

-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
    public static int e(...);
}

#Specifies that the access modifiers of classes and class members may be broadened during processing.
#Counter-indication: you probably shouldn't use this option when processing code that is to be used as a library, since classes and class members that weren't designed to be public in the API may become public.
-allowaccessmodification

# Obfuscation Options
#-dontobfuscate
-dontusemixedcaseclassnames

#For app
#-keepattributes *Annotation*

-optimizationpasses 10
-dontskipnonpubliclibraryclassmembers
-printmapping proguardMapping.txt
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
-keepattributes *Annotation*,InnerClasses, Signature, SourceFile,LineNumberTable, Exceptions

-renamesourcefileattribute SourceFile

# OKHTTP
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-dontwarn org.conscrypt.**

# Gson
-keep class com.google.gson.** { *; }

# Sentry Java
-dontwarn javax.naming.**
-dontwarn javax.servlet.**
-dontwarn org.slf4j.**

# Only necessary if you downloaded the SDK jar directly instead of from maven.
-keep class com.shaded.fasterxml.jackson.** { *; }

# Sun Misc
-keep class sun.misc.Unsafe { *; }

# Ethereum Geth
-keep class org.ethereum.geth.** { *; }

# PBKDF2withHmacSHA512
-keep class org.spongycastle.** { *; }

# V8 Render
-keep class android.support.v8.renderscript.** { *; }

# Xin Ge
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep class com.tencent.android.tpush.** {* ;}
-keep class com.tencent.mid.** {* ;}
-keep class com.qq.taf.jce.** {*;}


# Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
-dontwarn com.bumptech.glide.load.resource.bitmap.VideoDecoder

# Jackson
-keep @com.fasterxml.jackson.annotation.JsonIgnoreProperties class * { *; }
-keep class com.fasterxml.** { *; }
-keep class org.codehaus.** { *; }
-keepnames class com.fasterxml.jackson.** { *; }
-keepclassmembers public final enum com.fasterxml.jackson.annotation.JsonAutoDetect$Visibility {
    public static final com.fasterxml.jackson.annotation.JsonAutoDetect$Visibility *;
}

# Kotlin
-keep class kotlin.** { *; }
-keep class kotlin.Metadata { *; }
-dontwarn kotlin.**
-keepclassmembers class **$WhenMappings { <fields>; }
-keepclassmembers class kotlin.Metadata { public <methods>; }
-assumenosideeffects class kotlin.jvm.internal.Intrinsics {
    static void checkParameterIsNotNull(java.lang.Object, java.lang.String);
}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}

# GoldStone
-dontwarn io.goldstone.blockchain.module.common.tokenpayment.gasselection.view.**
-dontwarn io.goldstone.blockchain.module.common.walletimport.**
-dontwarn io.goldstone.blockchain.module.entrance.starting.view.**
-dontwarn io.goldstone.blockchain.module.common.tokenpayment.addressselection.presenter.**
-dontwarn io.goldstone.blockchain.common.base.baseoverlayfragment.overlayview.**
-dontwarn io.goldstone.blockchain.module.home.wallet.tokenmanagement.tokenmanagement.presenter.**
-adaptclassstrings com.example.Test

# Anko
-dontwarn kotlin.jvm.internal.Intrinsics

-dontwarn org.bitcoinj.store.**

# Event Bus
-keepattributes *Annotation*
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }