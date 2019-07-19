package com.example.pomodoro;

import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

/*
    TODO: When Pause or Stop clicked after the timer is stoped, the Progress bar doesn't react.

*/
public class MainActivity extends AppCompatActivity {

    static final long RESET_VALUE = 8000;
    private TextView timerTextView;
    private long timeLeftMilliseconds = RESET_VALUE;
    private CountDownTimer countdownTimer;
    private ProgressBar mProgressBar;
    private int mProgressStatus = 100;
    private boolean isStartClicked = false;
    private boolean isTimerPaused = false;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        timerTextView = (TextView) findViewById(R.id.timerView);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mProgressBar.setProgress(mProgressStatus);
        updateTimer();
    }

    public void startButtonOnClick(View view) {
        if(!isTimerPaused) {
            startProgressBar();
        }
        else {
            startCountdownTimer();
            isTimerPaused = false;
        }
    }

    public void pauseButtonOnClick(View view) {
        try {
            if(countdownTimer == null) {
                return;
            }
            long tempTimeLeft = timeLeftMilliseconds;
            isTimerPaused = true;
            countdownTimer.cancel();
            timeLeftMilliseconds = tempTimeLeft;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopButtonOnClick(View view) {
        try {
            if(countdownTimer == null) {
                return;
            }
            countdownTimer.cancel();
            resetCountDownTimer();
            mProgressStatus = 0;
            isTimerPaused = false;
        } catch (Exception e) {
            e.printStackTrace();
        }

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
        if(isStartClicked) {
            return;
        }
        else {
            startCountdownTimer();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    isStartClicked = true;
                    while(mProgressStatus > 0) {
                        if(isTimerPaused) {
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
                                Thread.sleep(RESET_VALUE/100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    mProgressStatus = 100;
                    mProgressBar.setProgress(mProgressStatus);
                    isStartClicked = false;
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            resetCountDownTimer();
                        }
                    });
                }
            }).start();
        }
    }

    public void resetCountDownTimer() {
        timeLeftMilliseconds = RESET_VALUE;
        updateTimer();
    }

}
