package com.example.pomodoro;

import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    static final long RESET_VALUE = 10000;
    private TextView timerTextView;
    private long timeLeftMilliseconds = RESET_VALUE;
    private CountDownTimer countdownTimer;
    private ProgressBar mProgressBar;
    private int mProgressStatus = 100;
    private Handler mHandler = new Handler();
    private TimerState timerState = TimerState.NOTEXISTING;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        timerTextView = findViewById(R.id.timerView);
        mProgressBar = findViewById(R.id.progressBar);
        mProgressBar.setProgress(mProgressStatus);
        updateTimer();
    }

    public void startButtonOnClick(View view) {
        switch (timerState) {
            case NOTEXISTING:
                startProgressBar();
                timerState = TimerState.WORKING;
                break;
            case WORKING:
                break;
            case PAUSED:
                timerState = TimerState.WORKING;
                startCountdownTimer();
                break;
            case STOPPED:
                startProgressBar();
                timerState = TimerState.WORKING;
                break;
        }
    }

    public void pauseButtonOnClick(View view) {
        switch (timerState) {
            case NOTEXISTING:
                break;
            case WORKING:
                pauseCountDownTimer();
                break;
            case PAUSED:
                break;
            case STOPPED:
                break;
        }
    }

    public void stopButtonOnClick(View view) {
        switch (timerState) {
            case NOTEXISTING:
                break;
            case WORKING:
                stopCountDownTimer();
                stopProgressBar();
                resetCountDownTimer();
                resetProgressBar();
                timerState = TimerState.STOPPED;
                break;
            case PAUSED:
                stopCountDownTimer();
                stopProgressBar();
                resetCountDownTimer();
                resetProgressBar();
                timerState = TimerState.STOPPED;
                break;
            case STOPPED:
                break;
        }
    }

    public void pauseCountDownTimer() {
        try {
            if(countdownTimer == null) {
                return;
            }
            long tempTimeLeft = timeLeftMilliseconds;
            countdownTimer.cancel();
            timeLeftMilliseconds = tempTimeLeft;
            timerState = TimerState.PAUSED;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void resetProgressBar() {

    }

    public void startCountdownTimer() {
        countdownTimer = new CountDownTimer(timeLeftMilliseconds, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftMilliseconds = millisUntilFinished;
                updateTimer();
            }

            @Override
            public void onFinish() {
            }
        }.start();
    }

    public void updateTimer() {
        int minutes = (int) timeLeftMilliseconds / 60000;
        int seconds = (int) timeLeftMilliseconds % 60000 / 1000;

        String timeLeftText = "" + minutes + ":";
        if(seconds < 10) {
            timeLeftText += "0" + seconds;
        }
        else {
            timeLeftText += seconds;
        }
        timerTextView.setText(timeLeftText);
    }

    public void startProgressBar() {
        startCountdownTimer();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mProgressStatus > 0) {
                    if (timerState == TimerState.PAUSED) {
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        mProgressStatus--;
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                mProgressBar.setProgress(mProgressStatus);
                            }
                        });
                        try {
                            Thread.sleep(RESET_VALUE / 100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                mProgressStatus = 100;
                mProgressBar.setProgress(mProgressStatus);
                timerState = TimerState.STOPPED;
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        resetCountDownTimer();
                    }
                });
            }
        }).start();
    }

    public void resetCountDownTimer() {
        timeLeftMilliseconds = RESET_VALUE;
        updateTimer();
    }

    public void stopProgressBar() {
        mProgressStatus = 0;
    }

    public void stopCountDownTimer() {
        if(countdownTimer == null) {
            return;
        } else {
            countdownTimer.cancel();
        }
    }

}

