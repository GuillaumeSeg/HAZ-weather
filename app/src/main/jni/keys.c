#include <jni.h>

JNIEXPORT jstring JNICALL
Java_eu_gsegado_hazweather_repository_WeatherRepository_getDarkSkyApiKey(JNIEnv *env, jobject instance) {
    return (*env)->NewStringUTF(env, "0ca9640efdf53c985f391d1bd385098b");
}