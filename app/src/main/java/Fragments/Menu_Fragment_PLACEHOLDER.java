package Fragments;

import android.app.Activity;
import android.app.Fragment;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;

import lesdevoreurs.bon_manger.DBHelper;
import lesdevoreurs.bon_manger.R;

/**
 * Created by virgile on 17/04/2015.
 */
public class Menu_Fragment_PLACEHOLDER extends Fragment {

    ArrayAdapter<CharSequence> adapter;
    View rootView;
    //Map<int, dailyMenu> menuBuckets;

    public Menu_Fragment_PLACEHOLDER() {
    }



    public static void receiveRecipe(DBHelper dbh, String titre, String image, String description, String tempsCuisson,
                                     String tempsTotal, String instructions, ArrayList<String> ingreNom,
                                     ArrayList<String> ingreNum, ArrayList<String> ingreMet, String id) {
        //We receive the informations of the recipe to add from search
        //Here we add it to memory
    }

    public static void receiveRecipe(DBHelper dbh, String titre, String image, String description, String tempsCuisson,
                                     String tempsTotal, String instructions, Cursor c, String id) {
        //We receive the informations of the recipe to add from current recipe
        //Here we add it to memory
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //if the view doesn't exist yet, create it
        if (rootView == null)
        rootView = inflater.inflate(R.layout.menu_layout_placeholder, container, false);
/*
        //Init the objects assignation
        if (showDayButton == null)
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
*/
        return rootView;
    }

    public class SpinnerActivity extends Activity implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view,
                                   int pos, long id) {
            // An item was selected. You can retrieve the selected item using
            // parent.getItemAtPosition(pos)
        }

        public void onNothingSelected(AdapterView<?> parent) {
            // Another interface callback
        }
    }

    public class dailyMenu
    {
        public dailyMenu ()
        {

        }
    }

    public class recipeItem
    {
        public recipeItem()
        {

        }
    }
}
