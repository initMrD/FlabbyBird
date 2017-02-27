package com.initmrd.flabbybird.util;

import android.media.MediaRecorder;

/**
 * Created by initMrd@gmail.com on 17/2/27.
 */

public class AudioUtil {
    private MediaRecorder mediaRecorder;

    private String FolderPath;



    private void startRecode(){
        if(mediaRecorder == null){
            mediaRecorder = new MediaRecorder();
        }
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
    }
}
