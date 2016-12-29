package com.cl.slack.audio_foreign;

/**
 * Created by slack on 2016/12/27 15:29.
 */

public class AudioJNI {

    static {
        try {
            System.loadLibrary("opensl_audio");
        } catch (UnsatisfiedLinkError e) {
            System.err.println("native code library failed to load.\n" + e);
            System.exit(1);
        }
    }

    public final static native boolean startRecodeAudio();

    public final static native boolean stopRecodeAudio();

    public final static native boolean startPlayAudio();

    public final static native boolean stopPlayAudio();
}
