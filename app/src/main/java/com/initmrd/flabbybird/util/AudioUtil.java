package com.initmrd.flabbybird.util;

import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;

import java.io.IOException;

/**
 * Created by initMrd@gmail.com on 17/2/27.
 */

public class AudioUtil {
    private MediaRecorder mediaRecorder;

    private String folderPath;

    private String filePath;

    private Handler mHandler = new Handler();

    private onAudioStatusUpdateListener onAudioStatusUpdateListener;

    public AudioUtil() {
        this(Environment.getExternalStorageDirectory()+"/record/");
    }

    public AudioUtil(String folderPath) {
        this.folderPath = folderPath;
    }

    public void startRecode(){
        if(mediaRecorder == null){
            mediaRecorder = new MediaRecorder();
        }
        filePath = folderPath + System.currentTimeMillis() + ".amr";
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(filePath);

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopRecode(){
        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;
    }

    public void setOnAudioStatusUpdateListener(onAudioStatusUpdateListener listener){
        onAudioStatusUpdateListener = listener;
    }

    public interface onAudioStatusUpdateListener{

        public void updata(double db);

        public void onStop(String filePath);
    }

    private void updataMicStatic(){

    }

    private Runnable updataMicStatusTimer = new Runnable() {
        @Override
        public void run() {
            updataMicStatic();
        }
    };
}
