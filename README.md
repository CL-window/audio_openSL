form  https://audioprograming.wordpress.com/2012/03/03/android-audio-streaming-with-opensl-es-and-the-ndk/
老外的代码提供的功能真的不错，看看代码就知道了
opensl_io.h 用到的主要的四个函数
分别是 打开/关闭android 音频设备 写入音频数据和读取音频数据
看c 代码之前,关于指针的几个小知识点需要先温习一下下
/**  needs know
 *  ->  是在引用结构体中的变量
 *      结构体实例可以通过使用 ‘.’ 符号访问变量。对于结构体实例的指针，我们可以通过 ‘->’ 符号访问变量
 *   *  当用在声明一个变量时，*表示这里声明了一个指针。其它情况用到*表示指针的取值
 *   &  地址操作符，用来引用一个内存地址
 */

/*
Open the audio device with a given sampling rate (sr), input and output channels and IO buffer size
in frames. Returns a handle to the OpenSL stream
*/
OPENSL_STREAM* android_OpenAudioDevice(int sr, int inchannels, int outchannels, int bufferframes);
/*
Close the audio device
*/
void android_CloseAudioDevice(OPENSL_STREAM *p);
/*
Read a buffer from the OpenSL stream *p, of size samples. Returns the number of samples read.
*/
int android_AudioIn(OPENSL_STREAM *p, float *buffer,int size);
/*
Write a buffer to the OpenSL stream *p, of size samples. Returns the number of samples written.
*/
int android_AudioOut(OPENSL_STREAM *p, float *buffer,int size);

需要注意的是,播放和录制audio需要在子线程里运行,我使用的是 HandlerThread
只是使用了一个文件 opensl_io.c 和 opensl_io.h
原作者有三个文件  opensl_io.c opensl_io2.c 和 opensl_io3.c
opensl_io.c 是使用线程锁来控制录制和播放
opensl_io2.c 使用 google 推荐的 回调 SLAndroidSimpleBufferQueueItf
opensl_io3.c 也是使用回调,不过播放回调包含了audio信息