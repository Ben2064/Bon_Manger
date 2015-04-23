package Timer;


import android.app.Activity;
import android.app.ListActivity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import lesdevoreurs.bon_manger.R;

/**
 * Created by BenZen on 20/04/15.
 */
 /*
public class timer_item extends Activity
{
    ProgressBar timerBar;
    TextView timerName;
    TextView timeRemainText;
    int timeRemain;
    Button deleteTimer;
    View view;
    CountDownTimer countDown;

    @Override
    public void onCreate(Bundle savedState)
    {
        super.onCreate(savedState);
        setContentView(R.layout.timer_item);

        //Sets up the timer
        timerBar = (ProgressBar) view.findViewById(R.id.timerIcon);
        Animation anim = new RotateAnimation(0.0f, 90.0f, 250f, 273f);
        anim.setFillAfter(true);
        timerBar.startAnimation(anim);

        //Sets up the button listener

    }
    public void onClick()
    {
        //delete timer if button
    }

    public void setTimerName(String name)
    {
        timerName.setText(name);
    }

    public void setTime(int time)
    {
        countDown = new CountDownTimer(time, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                //Update time remain and display!
            }

            @Override
            public void onFinish() {
                //Alert User
            }
        }.start();
    }

    public time getTime(){
        return timeRemain;
    }

    public String getName(){
        return timerName.getText();
    }

    public class TimerClass extends CountDownTimer
    {
        long totalTime;

        public TimerClass(long millisInFuture, long countDownInterval)
        {
            super(millisInFuture, countDownInterval);
            this.totalTime = millisInFuture;
        }

        @Override
        public void onTick(long millisUntilFinished)
        {
            //Updates time remaining and display!
            long millisec = millisUntilFinished;
            String timeShown;
            int percentComplete = (int) (millisUntilFinished/totalTime);

            if (millisec < 60000) {
                //show only seconds if < 1 min
                timeShown = String.format("%02d", TimeUnit.MILLISECONDS.toSeconds(millisec));
            }
            else if (millisec < 600000) {
                //show min:sec if < 10 min
                timeShown = String.format("%02d:%d20", TimeUnit.MILLISECONDS.toMinutes(millisec),
                        TimeUnit.MILLISECONDS.toSeconds(millisec) - TimeUnit.HOURS.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisec)));
            }
            else if (millisec < 3600000) {
                //show only minutes if < 1 hour
                timeShown = String.format("%02d", TimeUnit.MILLISECONDS.toMinutes(millisec));
            }
            else {
                //show hours:min
                timeShown = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toHours(millisec),
                        TimeUnit.MILLISECONDS.toMinutes(millisec) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisec)));
            }


            timeRemainText.setText(timeShown);
        }

        @Override
        public void onFinish()
        {
            //Alert User
        }

    }


}*/