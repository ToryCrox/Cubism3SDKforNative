/*
 * Copyright(c) Live2D Inc. All rights reserved.
 *
 * Use of this source code is governed by the Live2D Open Software license
 * that can be found at http://live2d.com/eula/live2d-open-software-license-agreement_en.html.
 */

#include <jni.h>
#include <LAppLive2DManager.hpp>
#include "JniBridgeC.hpp"
#include "LAppDelegate.hpp"
#include "LAppPal.hpp"

using namespace Csm;

static JavaVM* g_JVM; // JavaVM is valid for all threads, so just save it globally
static jclass  g_JniBridgeJavaClass;
static jmethodID g_LoadFileMethodId;
//static jmethodID g_MoveTaskToBackMethodId;
static jmethodID g_hitTest;
static jmethodID g_getDefaultModelFile;

JNIEnv* GetEnv()
{
    JNIEnv* env = NULL;
    g_JVM->GetEnv(reinterpret_cast<void **>(&env), JNI_VERSION_1_6);
    return env;
}

// The VM calls JNI_OnLoad when the native library is loaded
jint JNICALL JNI_OnLoad(JavaVM* vm, void* reserved)
{
    g_JVM = vm;

    JNIEnv *env;
    if (vm->GetEnv(reinterpret_cast<void **>(&env), JNI_VERSION_1_6) != JNI_OK)
    {
        return JNI_ERR;
    }

    jclass clazz = env->FindClass("com/mimikko/live2d3/JniBridgeJava");
    g_JniBridgeJavaClass = reinterpret_cast<jclass>(env->NewGlobalRef(clazz));
    g_LoadFileMethodId = env->GetStaticMethodID(g_JniBridgeJavaClass, "LoadFile", "(Ljava/lang/String;)[B");
    //g_MoveTaskToBackMethodId = env->GetStaticMethodID(g_JniBridgeJavaClass, "moveTaskToBack", "()V");
    g_hitTest = env->GetStaticMethodID(g_JniBridgeJavaClass, "hitTest", "(Ljava/lang/String;)V");
    g_getDefaultModelFile = env->GetStaticMethodID(g_JniBridgeJavaClass, "getDefaultModelFile",
            "(Ljava/lang/String;)Ljava/lang/String;");
    return JNI_VERSION_1_6;
}

void JNICALL JNI_OnUnload(JavaVM *vm, void *reserved)
{
    JNIEnv *env = GetEnv();
    env->DeleteGlobalRef(g_JniBridgeJavaClass);
}

char* JniBridgeC::LoadFileAsBytesFromJava(const char* filePath, unsigned int* outSize)
{
    JNIEnv *env = GetEnv();

    // ファイルロード
    jbyteArray obj = (jbyteArray)env->CallStaticObjectMethod(g_JniBridgeJavaClass, g_LoadFileMethodId, env->NewStringUTF(filePath));
    if (obj == NULL){
        return NULL;
    }
    *outSize = static_cast<unsigned int>(env->GetArrayLength(obj));

    char* buffer = new char[*outSize];
    env->GetByteArrayRegion(obj, 0, *outSize, reinterpret_cast<jbyte *>(buffer));

    return buffer;
}

/*void JniBridgeC::MoveTaskToBack()
{
    JNIEnv *env = GetEnv();

    // アプリ終了
    env->CallStaticVoidMethod(g_JniBridgeJavaClass, g_MoveTaskToBackMethodId, NULL);
}*/

void JniBridgeC::hitTest(const char* action)
{
    JNIEnv *env = GetEnv();

    // アプリ終了
    env->CallStaticVoidMethod(g_JniBridgeJavaClass, g_hitTest, env->NewStringUTF(action));
}

char* JniBridgeC::getDefaultModelFile(const char* key)
{
    JNIEnv *env = GetEnv();

    jstring p = env->NewStringUTF(key);
    // アプリ終了
    jstring  result = static_cast<jstring>(env->CallStaticObjectMethod(g_JniBridgeJavaClass,
            g_getDefaultModelFile, p));
    if (result != NULL){
        char* cstr = const_cast<char *>(env->GetStringUTFChars(result, 0));
        return cstr;
    }else {
        return NULL;
    }
}

extern "C"
{
    JNIEXPORT void JNICALL
    Java_com_mimikko_live2d3_JniBridgeJava_nativeOnStart(JNIEnv *env, jclass type)
    {
        LAppDelegate::GetInstance()->OnStart();
    }

    JNIEXPORT void JNICALL
    Java_com_mimikko_live2d3_JniBridgeJava_nativeOnPause(JNIEnv *env, jclass type)
    {
        LAppDelegate::GetInstance()->OnPause();
    }

    JNIEXPORT void JNICALL
    Java_com_mimikko_live2d3_JniBridgeJava_nativeOnStop(JNIEnv *env, jclass type)
    {
        LAppDelegate::GetInstance()->OnStop();
    }

    JNIEXPORT void JNICALL
    Java_com_mimikko_live2d3_JniBridgeJava_nativeOnDestroy(JNIEnv *env, jclass type)
    {
        LAppDelegate::GetInstance()->OnDestroy();
    }

    JNIEXPORT void JNICALL
    Java_com_mimikko_live2d3_JniBridgeJava_nativeOnSurfaceCreated(JNIEnv *env, jclass type)
    {
        LAppDelegate::GetInstance()->OnSurfaceCreate();
    }

    JNIEXPORT void JNICALL
    Java_com_mimikko_live2d3_JniBridgeJava_nativeOnSurfaceChanged(JNIEnv *env, jclass type, jint width, jint height)
    {
        LAppDelegate::GetInstance()->OnSurfaceChanged(width, height);
    }

    JNIEXPORT void JNICALL
    Java_com_mimikko_live2d3_JniBridgeJava_nativeOnDrawFrame(JNIEnv *env, jclass type)
    {
        LAppDelegate::GetInstance()->Run();
    }

    JNIEXPORT void JNICALL
    Java_com_mimikko_live2d3_JniBridgeJava_nativeOnTouchesBegan(JNIEnv *env, jclass type, jfloat pointX, jfloat pointY)
    {
        LAppDelegate::GetInstance()->OnTouchBegan(pointX, pointY);
    }

    JNIEXPORT void JNICALL
    Java_com_mimikko_live2d3_JniBridgeJava_nativeOnTouchesEnded(JNIEnv *env, jclass type, jfloat pointX, jfloat pointY)
    {
        LAppDelegate::GetInstance()->OnTouchEnded(pointX, pointY);
    }

    JNIEXPORT void JNICALL
    Java_com_mimikko_live2d3_JniBridgeJava_nativeOnTouchesMoved(JNIEnv *env, jclass type, jfloat pointX, jfloat pointY)
    {
        LAppDelegate::GetInstance()->OnTouchMoved(pointX, pointY);
    }

    JNIEXPORT void JNICALL
    Java_com_mimikko_live2d3_JniBridgeJava_nativeOnTouchesBeganF(JNIEnv *env, jclass type,
                                                              jfloat pointX, jfloat pointY,
                                                              jfloat pointX2, jfloat pointY2) {

        LAppDelegate::GetInstance()->OnTouchBegan(pointX, pointY, pointX2, pointY2);

    }

    JNIEXPORT void JNICALL
    Java_com_mimikko_live2d3_JniBridgeJava_nativeOnTouchesMovedF(JNIEnv *env, jclass type,
                                                                  jfloat pointX, jfloat pointY,
                                                                  jfloat pointX2, jfloat pointY2) {
        LAppPal::PrintLog("[APP]nativeOnTouchesMoved x1:%.2f, y1:%.2f, x2:%.2f, y2:%.2f", pointX, pointY, pointX2, pointY2);
        LAppDelegate::GetInstance()->OnTouchMoved(pointX, pointY, pointX2, pointY2);
    }

    JNIEXPORT void JNICALL
    Java_com_mimikko_live2d3_JniBridgeJava_nativeLoadModel(JNIEnv *env, jclass type, jstring modelPath_,
            jfloatArray matrixArr_) {
        const char *c_str = env->GetStringUTFChars(modelPath_, 0);
        float* arr = NULL;
        if (matrixArr_ != NULL){
            arr = env->GetFloatArrayElements(matrixArr_, NULL);
            jsize size = env->GetArrayLength(matrixArr_);
            env->ReleaseFloatArrayElements(matrixArr_, arr, 0);
        }
        LAppDelegate::GetInstance()->LoadModel(c_str, arr);
        env->ReleaseStringUTFChars(modelPath_, c_str);
    }

    JNIEXPORT void JNICALL
    Java_com_mimikko_live2d3_JniBridgeJava_nativeStartMotion(JNIEnv *env, jclass type, jstring modelPath_,
                                                         jfloat fadeInSeconds, jfloat fadeOutSeconds) {
        const char *motionPath = env->GetStringUTFChars(modelPath_, 0);
        LAppLive2DManager::GetInstance()->startMotion(motionPath, fadeInSeconds, fadeOutSeconds);
        env->ReleaseStringUTFChars(modelPath_, motionPath);
    }

    JNIEXPORT jfloatArray JNICALL
    Java_com_mimikko_live2d3_JniBridgeJava_nativeGetMatrixArray(JNIEnv *env, jclass type) {

        float* array = LAppLive2DManager::GetInstance()->getViewMatrixArray();
        if (array == NULL){
            return NULL;
        }
        int size = 16;
        jfloatArray result = env->NewFloatArray(size);
        env->SetFloatArrayRegion(result, 0, size, array);
        return result;
    }

    JNIEXPORT void JNICALL
    Java_com_mimikko_live2d3_JniBridgeJava_nativeStartLipSyncMotion(JNIEnv *env, jclass type,
                                                                    jstring modelPath_,
                                                                    jfloat fadeInSeconds,
                                                                    jfloat fadeOutSeconds) {
        const char *modelPath = env->GetStringUTFChars(modelPath_, 0);


        env->ReleaseStringUTFChars(modelPath_, modelPath);
    }
}
