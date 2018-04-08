package com.sun.amy.utils;

import android.media.MediaRecorder;

import java.io.File;
import java.io.IOException;

/**
 * MediaRecorderThread class.
 */
public class MediaRecorderThread extends Thread {

    private MediaRecorder mMediaRecorder;
    private boolean mIsRecording;
    private String mAudioFilePath;

    public boolean isRecording() {
        return this.isAlive() && mIsRecording;
    }

    public void startRecording(String filePath) {
        if (!isRecording()) {
            mAudioFilePath = filePath;
            start(); // start recording thread
        }
    }

    public void stopRecording() {
        try {
            mMediaRecorder.stop();
            mIsRecording = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            File file = new File(mAudioFilePath);
            mMediaRecorder = new MediaRecorder();
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            mMediaRecorder.setOutputFile(file.getAbsolutePath());

            mMediaRecorder.prepare();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mMediaRecorder.start();
    }
}
