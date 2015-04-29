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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Map;

import lesdevoreurs.bon_manger.DBHelper;
import lesdevoreurs.bon_manger.R;

/**
 * Created by virgile on 17/04/2015.
 */
public class Menu_Fragment_PLACEHOLDER extends Fragment {

    ArrayAdapter<CharSequence> adapter;
    View rootView;
    DatePicker datePicker;
    Button showButton;
    ListView listMenu;
    int currentDate;

    Map<Integer, dailyMenu> menuBuckets;

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

        //Init the objects assignation
        if (showButton == null)
        {
            datePicker = (DatePicker) rootView.findViewById((R.id.datePicker));
            showButton = (Button) rootView.findViewById((R.id.showDay));
            listMenu = (ListView) rootView.findViewById(R.id.listMenu);

            showButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    //Change the day
                }
            });
        }

        return rootView;
    }

    //Contains all the info for one day's worth of menu
    public class dailyMenu
    {
        public dailyMenu ()
        {

        }
    }
}
