package Fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import lesdevoreurs.bon_manger.MainActivity;
import lesdevoreurs.bon_manger.R;

/**
 *
 * This is the java file for most of the timer-related stuff. It holds the database
 * for them and utility classes/methods.
 */



public class Timer_Fragment extends Fragment
{

    ListView timerList;
    TextView addTime, addName;
    Button addBtn;
    View view;
    static ArrayList<TimerClass> timerArray = new ArrayList<TimerClass>();

    public Timer_Fragment(){};

    public static int getNumberOfTimer()
    {
        return timerArray.size();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        //if the view doesn't exist yet, create it
        if(view == null)
            view = inflater.inflate(R.layout.timer_main_view, container, false);

        //Init the objects assignation
        if (timerList == null)
        {
            timerList = (ListView) view.findViewById(R.id.timerList);
            addTime = (EditText) view.findViewById(R.id.editTimerTime);
            addName = (EditText) view.findViewById(R.id.editTimerName);
            addBtn = (Button) view.findViewById((R.id.addBtn));


            addTime.setSelectAllOnFocus(true);

            addName.setSelectAllOnFocus(true);

            addBtn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    //Convert the time data into milliseconds
                    String timeText = String.valueOf(addTime.getText());
                    String[] timeParts = timeText.split(":");

                    int strToMillis;
                    if (timeParts.length == 0)
                    {
                        strToMillis = 0;
                    }
                    else if (timeParts.length == 1)
                    {
                        strToMillis =
                                Integer.parseInt(timeParts[0])*1000;
                    }
                    else if (timeParts.length == 2)
                    {
                        strToMillis =
                                Integer.parseInt(timeParts[0])*60000
                              + Integer.parseInt(timeParts[1])*1000;
                    }
                    else
                    {
                        strToMillis =
                                Integer.parseInt(timeParts[0])*3600000
                              + Integer.parseInt(timeParts[1])*60000
                              + Integer.parseInt(timeParts[2])*1000;
                    }

                    TimerClass realTimer = new TimerClass(strToMillis, 1000, String.valueOf(addName.getText()));

                    //Add a timer with specified values
                    boolean added = false;
                    if(timerArray.size() != 0) {
                        for (TimerClass c : timerArray) {
                            if (realTimer.totalTime <= c.millisec) {
                                timerArray.add(timerArray.indexOf(c), realTimer);
                                added = true;
                                break;
                            }
                        }
                    }
                    if (added == false)
                        timerArray.add(realTimer);

                    ((BaseAdapter) timerList.getAdapter()).notifyDataSetChanged();

                    //start the timer
                    realTimer.start();
                }
            });
        }

        timerList.setAdapter(new TimerAdapter(timerArray));

        return view;
    }

    public void onActivityCreated(final Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        if ((timerList.getAdapter()) != null)
        {
            timerList.setAdapter(new TimerAdapter(timerArray));
            ((BaseAdapter) timerList.getAdapter()).notifyDataSetChanged();
        }
    }

    public class TimerAdapter extends BaseAdapter
    {

        ArrayList<TimerClass> timerArray;
        LayoutInflater inflater;

        public TimerAdapter(ArrayList<TimerClass> timerArray)
        {
            inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            this.timerArray = timerArray;
        }

        @Override
        public int getCount() {
            if (timerArray == null)
                return 0;
            return timerArray.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent)
        {
            final TimerClass thisTimer = timerArray.get(position);
            View v = convertView;

            if (v == null) {
                v = inflater.inflate(R.layout.timer_item, parent, false);
            }

            //Put everything in the listview
            TextView name = (TextView) v.findViewById(R.id.timerName);
            TextView time = (TextView) v.findViewById(R.id.timeLeft);
            TextView timeUnit = (TextView) v.findViewById(R.id.timeUnit);
            ProgressBar progress = (ProgressBar) v.findViewById(R.id.timerIcon);
            Button delButton = (Button) v.findViewById(R.id.timerDelete);

            //Sets the infos for this timer
            name.setText(timerArray.get(position).name);
            time.setText(timerArray.get(position).timeShown);
            timeUnit.setText(timerArray.get(position).timeUnit);

            //Sets the progress for the circular bar, first two lines are a workaround
            //for a known bug...
            progress.setMax(0);
            progress.setProgress(0);
            progress.setMax((int) timerArray.get(position).totalTime);
            progress.setProgress((int) timerArray.get(position).millisec);

                //Delete button listener + onClick
                delButton.setOnClickListener(new View.OnClickListener()

                                             {
                                                 public void onClick(View v) {
                                                     //get the index of this timer
                                                     int index = timerArray.indexOf(thisTimer);
                                                     //checks if it exists just in case
                                                     if (index >= 0) {
                                                         //remove the timer from the database and update
                                                         timerArray.get(index).cancel();
                                                         timerArray.remove(index);
                                                         ((BaseAdapter) timerList.getAdapter()).notifyDataSetChanged();
                                                     }
                                                 }
                                             }

                );

            return v;
            }
        }

    public class TimerClass extends CountDownTimer
    {
        public long totalTime;
        public long millisec;
        public int percentComplete;
        public String timeShown;
        public String name = "VOID";
        public String timeUnit = "VOID";
        private TimerClass thisTimer = this;

        @Override
        public String toString()
        {
            return this.name;
        }

        public TimerClass(long millisInFuture, long countDownInterval, String name)
        {
            super(millisInFuture, countDownInterval);
            this.totalTime = millisInFuture;
            this.millisec = millisInFuture;
            this.timeShown = Integer.toString((int) millisInFuture);
            this.name = name;
        }

        @Override
        public void onTick(long millisUntilFinished)
        {
            //Updates time remaining and display!
            millisec = millisUntilFinished;
            percentComplete = (int) (millisec*100/totalTime);

            if (millisec < 60000) {
                //show only seconds if < 1 min
                timeShown = String.format("%02d", TimeUnit.MILLISECONDS.toSeconds(millisec));
                timeUnit = "sec";
            }
            else if (millisec < 600000) {
                //show min:sec if < 10 min
                timeShown = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(millisec),
                        TimeUnit.MILLISECONDS.toSeconds(millisec) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisec)));
                timeUnit = "min:sec";
            }
            else if (millisec < 3600000) {
                //show only minutes if < 1 hour
                timeShown = String.format("%02d", TimeUnit.MILLISECONDS.toMinutes(millisec));
                timeUnit = "min";
            }
            else {
                //show hours:min
                timeShown = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toHours(millisec),
                        TimeUnit.MILLISECONDS.toMinutes(millisec) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisec)));
                timeUnit = "h:min";
            }

            //update the adapter
            ((BaseAdapter) timerList.getAdapter()).notifyDataSetChanged();
        }

        @Override
        public void onFinish()
        {
            //Create a dialog box to alert the user
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

            // Title and message + button
            alertDialogBuilder.setTitle("Timer finished");
            alertDialogBuilder
                    .setMessage("The timer \"" + thisTimer.toString() + "\" has just finished!")
                    .setCancelable(false)
                    .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();

                            //get the index of this timer
                            int index = timerArray.indexOf(thisTimer);
                            //checks if it exists just in case
                            if (index >= 0)
                            {
                                //remove the finished timer from the database and update
                                timerArray.remove(index);
                                ((BaseAdapter) timerList.getAdapter()).notifyDataSetChanged();
                            }
                        }
                    });

            //display the dialog box
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }

    }
}
