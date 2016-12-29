//
// Created by slack on 2016/12/27.
//


#include <stdio.h>
#include <android/log.h>
#include "opensl_io.h"
#include "jni.h"

#define LOG(...) __android_log_print(ANDROID_LOG_DEBUG,"AudioDemo-JNI",__VA_ARGS__)

#define SAMPLERATE 44100
#define CHANNELS 1
#define PERIOD_TIME 20 //ms
#define FRAME_SIZE SAMPLERATE*PERIOD_TIME/1000
#define BUFFER_SIZE FRAME_SIZE*CHANNELS
#define TEST_CAPTURE_FILE_PATH "/sdcard/audio.pcm"

static volatile int g_loop_exit = 0;

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT jboolean JNICALL
Java_com_cl_slack_audio_1foreign_AudioJNI_startRecodeAudio(JNIEnv *env, jclass type) {

    FILE * fp = fopen(TEST_CAPTURE_FILE_PATH, "wb");
    if( fp == NULL ) {
        LOG("cannot open file (%s)\n", TEST_CAPTURE_FILE_PATH);
        return -1;
    }

    OPENSL_STREAM* stream = android_OpenAudioDevice(SAMPLERATE, CHANNELS, CHANNELS, FRAME_SIZE);
    if (stream == NULL) {
        fclose(fp);
        LOG("failed to open audio device ! \n");
        return JNI_FALSE;
    }

    int samples;
    float buffer[BUFFER_SIZE];
    g_loop_exit = 0;
    while (!g_loop_exit) {
        samples = android_AudioIn(stream, buffer, BUFFER_SIZE);
        if (samples < 0) {
            LOG("android_AudioIn failed !\n");
            break;
        }
        if (fwrite((unsigned char *)buffer, samples*sizeof(short), 1, fp) != 1) {
            LOG("failed to save captured data !\n ");
            break;
        }
        LOG("capture %d samples !\n", samples);
    }

    android_CloseAudioDevice(stream);
    fclose(fp);

    LOG("nativeStartCapture completed !");

    return JNI_TRUE;

}

JNIEXPORT jboolean JNICALL
Java_com_cl_slack_audio_1foreign_AudioJNI_stopRecodeAudio(JNIEnv *env, jclass type) {

    g_loop_exit = 1;
    return JNI_TRUE;

}

JNIEXPORT jboolean JNICALL
Java_com_cl_slack_audio_1foreign_AudioJNI_startPlayAudio(JNIEnv *env, jclass type) {

    FILE * fp = fopen(TEST_CAPTURE_FILE_PATH, "rb");
    if( fp == NULL ) {
        LOG("cannot open file (%s) !\n",TEST_CAPTURE_FILE_PATH);
        return -1;
    }

    OPENSL_STREAM* stream = android_OpenAudioDevice(SAMPLERATE, CHANNELS, CHANNELS, FRAME_SIZE);
    if (stream == NULL) {
        fclose(fp);
        LOG("failed to open audio device ! \n");
        return JNI_FALSE;
    }

    int samples;
    float buffer[BUFFER_SIZE];
    g_loop_exit = 0;
    while (!g_loop_exit && !feof(fp)) {
        if (fread((unsigned char *)buffer, BUFFER_SIZE*2, 1, fp) != 1) {
            LOG("failed to read data \n ");
            break;
        }
        samples = android_AudioOut(stream, buffer, BUFFER_SIZE);
        if (samples < 0) {
            LOG("android_AudioOut failed !\n");
        }
        LOG("playback %d samples !\n", samples);
    }

    android_CloseAudioDevice(stream);
    fclose(fp);

    LOG("nativeStartPlayback completed !");

    return JNI_TRUE;

}

JNIEXPORT jboolean JNICALL
Java_com_cl_slack_audio_1foreign_AudioJNI_stopPlayAudio(JNIEnv *env, jclass type) {

    g_loop_exit = 1;
    return JNI_TRUE;

}

#ifdef __cplusplus
}
#endif