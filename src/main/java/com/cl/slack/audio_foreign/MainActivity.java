package com.cl.slack.audio_foreign;

import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.cl.slack.audio_foreign.opensl_example.opensl_example;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final int REQ_PERMISSION_AUDIO = 0x01;
    private TextView mInfo;
    private Button mForeignBtn,mRecordBtn,mPlayBtn;
    private Thread thread;

    // 在audio 录制和 播放 在线程里
    private HandlerThread mHandlerThread;
    private Handler mThreadHandler;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        mHandlerThread = new HandlerThread("audio_native");
        mHandlerThread.start();
        mThreadHandler = new Handler(mHandlerThread.getLooper());

    }


    private void initView() {
        mInfo = (TextView) findViewById(R.id.audio_info);
        mForeignBtn = (Button) findViewById(R.id.audio_from_foreign);
        mRecordBtn = (Button) findViewById(R.id.audio_recorde);
        mPlayBtn = (Button) findViewById(R.id.audio_play);


        thread = new Thread() {
            public void run() {
                setPriority(Thread.MAX_PRIORITY);
                opensl_example.start_process();
            }
        };

        mForeignBtn.setOnClickListener(this);
        mRecordBtn.setOnClickListener(this);
        mPlayBtn.setOnClickListener(this);

    }

    public void onDestroy(){
        super.onDestroy();
        mHandlerThread.quit(); //释放资源
    }

    @Override
    public void onClick(View view) {
        if(checkPermission()) {
            switch (view.getId()) {
                case R.id.audio_from_foreign:
                    if (mForeignBtn.getTag() == null) {
                            mForeignBtn.setTag(this);

                            thread.start();
                            mInfo.setText("foreign start...");
                        } else {
                            mForeignBtn.setTag(null);

                            opensl_example.stop_process();
                            try {
                                thread.join();
                            } catch (InterruptedException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                            thread = null;
                            mInfo.setText("foreign stop...");
                    }
                    break;
                case R.id.audio_recorde:
                    if (mRecordBtn.getTag() == null) {
                        mRecordBtn.setTag(this);
                        mRecordBtn.setText("stop recode");
                        mPlayBtn.setEnabled(false);
                        mInfo.setText("foreign start...");
                        mThreadHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                AudioJNI.startRecodeAudio();
                            }
                        });
                    } else {
                        mRecordBtn.setText("start recode");
                        mRecordBtn.setTag(null);
                        mPlayBtn.setEnabled(true);
                        AudioJNI.stopRecodeAudio();
                        mInfo.setText("foreign stop...");
                    }
                    break;
                case R.id.audio_play:
                    if (mPlayBtn.getTag() == null) {
                        mPlayBtn.setTag(this);
                        mPlayBtn.setText("stop play");
                        mInfo.setText("foreign start...");
                        mThreadHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                AudioJNI.startPlayAudio();
                            }
                        });
                    } else {
                        mPlayBtn.setTag(null);
                        mPlayBtn.setText("start play");
                        AudioJNI.stopPlayAudio();
                        mInfo.setText("foreign stop...");
                    }
                    break;
            }
        }else {
            requestPermission();
        }

    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(),
                WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(),
                RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED &&
                result1 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new
                String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, REQ_PERMISSION_AUDIO);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQ_PERMISSION_AUDIO:
                if (grantResults.length > 0) {
                    boolean StoragePermission = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    boolean RecordPermission = grantResults[1] ==
                            PackageManager.PERMISSION_GRANTED;

                    if (StoragePermission && RecordPermission) {
                        showToast("Permission Granted");
                    } else {
                        showToast("Permission  Denied");
                    }
                }
                break;
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
