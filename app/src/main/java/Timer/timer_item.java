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
    public void onCreate()
    {
        setContentView(R.layout.timer_item);
        timerBar = (ProgressBar) view.findViewById(R.id.timerIcon);
        Animation anim = new RotateAnimation(0.0f, 90.0f, 250f, 273f);
        anim.setFillAfter(true);
        timerBar.startAnimation(anim);
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
}
*/