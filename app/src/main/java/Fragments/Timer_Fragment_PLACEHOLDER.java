package Fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
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
import java.util.Timer;

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
    ArrayList<Timer> timerArray;
    ArrayList<String> nameArray;
    ArrayList<Integer> timeArray;

    public Timer_Fragment_PLACEHOLDER(){};

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        //if the view doesn't exist yet, create it
        if(view == null)
            view = inflater.inflate(R.layout.timer_main_view, container, false);

        timerList.setAdapter(new TimerAdapter(timerArray, timeArray, nameArray));

        return view;
    }

    public void onActivityCreated(final Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        //initialize objects when created
        if (addBtn == null)
        {
            timerList = (ListView) getView().findViewById(R.id.timerList);
            addTime = (EditText) getView().findViewById(R.id.editTimerTime);
            addName = (EditText) getView().findViewById(R.id.editTimerTime);
            addBtn = (Button) getView().findViewById((R.id.addBtn));
        }

        addBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Add a timer with specified values
                timerArray.add(new Timer());
                timeArray.add(Integer.parseInt((String) addTime.getText()));
                nameArray.add((String) addName.getText());
                ((BaseAdapter) timerList.getAdapter()).notifyDataSetChanged();
            }
        });

    }

    public class TimerAdapter extends BaseAdapter
    {

        ArrayList<Timer> timerArray;
        ArrayList<String> nameArray;
        ArrayList<Integer> timeArray;
        LayoutInflater inflater;

        public TimerAdapter(ArrayList<Timer> timerArray, ArrayList<Integer> timeArray, ArrayList<String> nameArray)
        {
            inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            this.timerArray = timerArray;
            this.timeArray = timeArray;
            this.nameArray = nameArray;
        }

        @Override
        public int getCount() {
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

            View v = convertView;

            if (v == null) {
                v = inflater.inflate(R.layout.research_item, parent, false);
            }

            //Put everything in the listview
            TextView name = (TextView) v.findViewById(R.id.timerName);
            TextView time = (TextView) v.findViewById(R.id.timeLeft);
            ProgressBar progress = (ProgressBar) v.findViewById(R.id.timerIcon);
            Button button = (Button) v.findViewById(R.id.timerDelete);

            name.setText(nameArray.get(position));
            time.setText(timeArray.get(position));
            //!!!TEMPORARY VALUE!!!!
            progress.setProgress(90);

            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    //Delete this object!
                }
            });

            return v;
        }
    }
}
