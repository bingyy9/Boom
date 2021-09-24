//package com.boom.android.thread;
//
//import android.hardware.Camera;
//import android.hardware.display.DisplayManager;
//import android.hardware.display.VirtualDisplay;
//import android.media.CamcorderProfile;
//import android.media.MediaRecorder;
//import android.media.projection.MediaProjection;
//import android.os.Build;
//import android.util.Log;
//import android.view.SurfaceHolder;
//
//import com.boom.android.BoomApplication;
//import com.boom.android.log.Dogger;
//import com.boom.android.service.MediaRecordService;
//import com.boom.android.ui.adapter.repo.RecordParams;
//import com.boom.android.util.BoomHelper;
//import com.boom.android.util.DataUtils;
//import com.boom.android.util.FilesDirUtil;
//import com.boom.android.util.NotificationUtils;
//import com.boom.android.util.PrefsUtil;
//import com.boom.android.util.RecordHelper;
//
//import java.io.IOException;
//import java.util.Timer;
//import java.util.TimerTask;
//
//public class MediaRecordThread extends Thread implements MediaRecorder.OnErrorListener{
//    private static final String TAG = "RecordThread";
//    private MediaRecorder mediaRecorder;
//    private Camera mCamera;
//    private MediaProjection mediaProjection;
//    private VirtualDisplay virtualDisplay;
//    private RecordParams recordParams;
//
//    public MediaRecordThread(RecordParams recordParams, MediaProjection project) {
//        this.recordParams = recordParams;
//        mediaProjection = project;
//    }
//
//    @Override
//    public void run() {
//        startRecord();
//    }
//
//    public void startRecord(){
//        if (mediaProjection == null || RecordHelper.isRecording()) {
//            return;
//        }
//        RecordHelper.setRecording(true);
//
//        if(RecordHelper.isRecordCamera()){
//            showCameraFloatingWindow();
//        }
//
//        initRecorder();
//        createVirtualDisplay();
//        if(mediaRecorder != null) {
//            mediaRecorder.start();
//        }
//        return;
//    }
//
//    public void pauseRecord(){
//        if (!RecordHelper.isRecording() || RecordHelper.isRecordingPaused()) {
//            Dogger.i(Dogger.BOOM, "ignore", "MediaRecordService", "pauseRecord");
//            NotificationUtils.startRecordingNotification(BoomApplication.getInstance().getApplicationContext());
//            return;
//        }
//
//        Dogger.i(Dogger.BOOM, "", "MediaRecordService", "pauseRecord");
//        if(mediaRecorder != null){
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                mediaRecorder.pause();
//                RecordHelper.setRecordingPaused(true);
//                NotificationUtils.startRecordingNotification(BoomApplication.getInstance().getApplicationContext());
//            }
//        }
//    }
//
//    public void resumeRecord(){
//        if (!RecordHelper.isRecordingPaused()) {
//            Dogger.i(Dogger.BOOM, "recording is not paused, ignore.", "MediaRecordService", "resumeRecord");
//            NotificationUtils.startRecordingNotification(BoomApplication.getInstance().getApplicationContext());
//            return;
//        }
//
//        Dogger.i(Dogger.BOOM, "", "MediaRecordService", "resumeRecord");
//        if(mediaRecorder != null){
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                mediaRecorder.resume();
//                RecordHelper.setRecordingPaused(false);
//                NotificationUtils.startRecordingNotification(BoomApplication.getInstance().getApplicationContext());
//            }
//        }
//    }
//
//    public void stopRecord() {
//        Dogger.i(Dogger.BOOM, "", "MediaRecordService", "stopRecord");
//        if (!RecordHelper.isRecording()) {
//            return;
//        }
//        if (mediaRecorder != null) {
//            //设置后不会崩
//            mediaRecorder.setOnErrorListener(null);
//            mediaRecorder.setPreviewDisplay(null);
//            try {
//                mediaRecorder.stop();
//                mediaRecorder.reset();
//                mediaRecorder.release();
//                mediaRecorder = null;
//            }  catch (Exception e) {
//                Dogger.e(Dogger.BOOM, "", "MediaRecordService", "stopRecord", e);
//            }
//        }
//        RecordHelper.setRecording(false);
//        virtualDisplay.release();
//        mediaProjection.stop();
//        NotificationUtils.removeRecordingNotification(BoomApplication.getInstance().getApplicationContext());
//    }
//
//    private void initRecorder() {
//        try {
//            //TODO: pending to address below exception based on referer other github record screen
////            java.lang.RuntimeException: setAudioSource failed.
////            at android.media.MediaRecorder.setAudioSource(Native Method)
//            if(mediaRecorder == null) {
//                mediaRecorder = new MediaRecorder();
//                mediaRecorder.setOnErrorListener(this);
//            } else {
//                mediaRecorder.reset();
//            }
//
//            mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
//            if(PrefsUtil.isRecordAudio(BoomApplication.getInstance().getApplicationContext())){
//                mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//            }
//
//
//            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
//            mediaRecorder.setOutputFile(FilesDirUtil.getRecordFileWriteDir(BoomApplication.getInstance().getApplicationContext())
//                    + DataUtils.formatDate4RecordDefaultName(BoomApplication.getInstance().getApplicationContext(), System.currentTimeMillis()) + ".mp4");
//
//            if(PrefsUtil.isRecordAudio(BoomApplication.getInstance().getApplicationContext())){
//                mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
//                mediaRecorder.setAudioSamplingRate(PrefsUtil.getAudioSampleRate(BoomApplication.getInstance().getApplicationContext()));//44100, 48000
//                mediaRecorder.setAudioEncodingBitRate(PrefsUtil.getAudioBitrate(BoomApplication.getInstance().getApplicationContext()) * 1000);  //128 kbps
//                mediaRecorder.setAudioChannels(PrefsUtil.getAudioChannelInt(BoomApplication.getInstance().getApplicationContext()));
//            }
//            mediaRecorder.setVideoSize(recordParams.getWidth(), recordParams.getHeight());
//            mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
//            mediaRecorder.setVideoEncodingBitRate(PrefsUtil.getVideoBitrate(BoomApplication.getInstance().getApplicationContext()) * 1024 * 1024);
//            mediaRecorder.setVideoFrameRate(PrefsUtil.getVideoFrameRate(BoomApplication.getInstance().getApplicationContext()));
//
//            mediaRecorder.prepare();
//
//            NotificationUtils.startRecordingNotification(BoomApplication.getInstance().getApplicationContext());
//        } catch (IOException e) {
//            Dogger.e(Dogger.BOOM, "", "MediaRecordService", "initRecorder", e);
//        }
//    }
//
//    private void createVirtualDisplay() {
//        virtualDisplay = mediaProjection.createVirtualDisplay("MainScreen"
//                , recordParams.getWidth()
//                , recordParams.getHeight()
//                , recordParams.getDpi(),
//                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, mediaRecorder.getSurface(), null, null);
//    }
//
//    @Override
//    public void onError(MediaRecorder mediaRecorder, int i, int i1) {
//
//    }
//}
