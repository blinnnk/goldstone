//
// Created by Saith Kay on 2019-01-07.
//
#include <jni.h>
#include <string>

extern "C"
JNIEXPORT jstring JNICALL
Java_io_goldstone_blinnnk_common_jni_JniManager_getKey(
	JNIEnv *pEnv,
	jobject pThis) {
	return pEnv->NewStringUTF("gPBZ[5Ms");
}

extern "C"
JNIEXPORT jstring JNICALL
Java_io_goldstone_blinnnk_common_jni_JniManager_getDecryptKey(
	JNIEnv *pEnv,
	jobject pThis) {
	return pEnv->NewStringUTF("k+BXtgY");
}


