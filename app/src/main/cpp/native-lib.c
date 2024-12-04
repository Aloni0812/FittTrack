#include <jni.h>
#include <string.h>
#include <stdlib.h>
#include <time.h>
#include <stdbool.h>

JNIEXPORT jboolean JNICALL
Java_by_zhogol_fittrack_NativeLib_00024Companion_validatePasswords(
        JNIEnv *env, jobject obj, jstring password, jstring repeatPassword) {
    const char *passwordC = (*env)->GetStringUTFChars(env, password, 0);
    const char *repeatPasswordC = (*env)->GetStringUTFChars(env, repeatPassword, 0);

    jboolean result = (strcmp(passwordC, repeatPasswordC) == 0);

    (*env)->ReleaseStringUTFChars(env, password, passwordC);
    (*env)->ReleaseStringUTFChars(env, repeatPassword, repeatPasswordC);

    return result;
}

JNIEXPORT jint JNICALL
Java_by_zhogol_fittrack_NativeLib_00024Companion_generateUserId(JNIEnv *env, jobject obj) {
    srand((unsigned int)time(NULL));
    return (rand() % 90000000) + 10000000;
}
