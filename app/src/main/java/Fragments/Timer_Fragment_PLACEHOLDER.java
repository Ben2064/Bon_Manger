package Fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import lesdevoreurs.bon_manger.R;

/**
 * Created by Benoir on 17/04/2015.
 *
 * This is the java file for most of the timer-related stuff. It holds the dataase
 * for them and utility classes/methods.
 */



public class Timer_Fragment_PLACEHOLDER extends Fragment
{

    ListView timerList;
    TextView addTime, addName;
    Button addBtn;
    View view;
    ArrayList<TimerClass> timerArray = new ArrayList<TimerClass>();
    ArrayList<String> nameArray = new ArrayList<String>();
    ArrayList<String> timeArray = new ArrayList<String>();

    public Timer_Fragment_PLACEHOLDER(){};

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

                    TimerClass realTimer = new TimerClass(strToMillis, 1000);

                    //Add a timer with specified values
                    timerArray.add(realTimer);
                    timeArray.add(String.valueOf(addTime.getText()));
                    nameArray.add(String.valueOf(addName.getText()));
                    ((BaseAdapter) timerList.getAdapter()).notifyDataSetChanged();

                    //start the timer
                    realTimer.start();
                }
            });
        }

        timerList.setAdapter(new TimerAdapter(timerArray, timeArray, nameArray));

        return view;
    }

    public void onActivityCreated(final Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
    }

    public class TimerAdapter extends BaseAdapter
    {

        ArrayList<TimerClass> timerArray;
        ArrayList<String> nameArray;
        ArrayList<String> timeArray;
        LayoutInflater inflater;

        public TimerAdapter(ArrayList<TimerClass> timerArray, ArrayList<String> timeArray, ArrayList<String> nameArray)
        {
            inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            this.timerArray = timerArray;
            this.timeArray = timeArray;
            this.nameArray = nameArray;
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
        public View getView(int position, View convertView, ViewGroup parent)
        {
            final TimerClass thisTimer = timerArray.get(position);
            View v = convertView;

            if (v == null) {
                v = inflater.inflate(R.layout.timer_item, parent, false);
            }

            //Put everything in the listview
            TextView name = (TextView) v.findViewById(R.id.timerName);
            TextView time = (TextView) v.findViewById(R.id.timeLeft);
            ProgressBar progress = (ProgressBar) v.findViewById(R.id.timerIcon);
            Button delButton = (Button) v.findViewById(R.id.timerDelete);

            //Sets the infos for this timer
            name.setText(nameArray.get(position));
            time.setText(timerArray.get(position).timeShown);
            progress.setProgress(timerArray.get(position).percentComplete);

            //Delete button listener + onClick
            delButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    //get the index of this timer
                    int index = timerArray.indexOf(thisTimer);
                    //checks if it exists just in case
                    if (index >= 0)
                    {
                        //remove the timer from the database and update
                        timerArray.get(index).cancel();
                        timerArray.remove(index);
                        nameArray.remove(index);
                        timeArray.remove(index);
                        ((BaseAdapter) timerList.getAdapter()).notifyDataSetChanged();
                    }
                }
            });

            return v;
        }
    }

    public class TimerClass extends CountDownTimer
    {
        public long totalTime;
        public long millisec;
        public int percentComplete;
        public String timeShown;
        TimerClass thisTimer = this;

        public TimerClass(long millisInFuture, long countDownInterval)
        {
            super(millisInFuture, countDownInterval);
            this.totalTime = millisInFuture;
            this.millisec = millisInFuture;
            this.timeShown = Integer.toString((int) millisInFuture);
        }

        @Override
        public void onTick(long millisUntilFinished)
        {
            //Updates time remaining and display!
            millisec = millisUntilFinished;
            percentComplete = (int) (millisUntilFinished/totalTime);

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

            //update the adapter
            ((BaseAdapter) timerList.getAdapter()).notifyDataSetChanged();
        }

        @Override
        public void onFinish()
        {
            //Create a dialog box to alert the user
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

            // Title and message + button
            alertDialogBuilder.setTitle("TimersClasses");
            alertDialogBuilder
                    .setMessage("A timer has just finished!")
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
                                nameArray.remove(index);
                                timeArray.remove(index);
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
