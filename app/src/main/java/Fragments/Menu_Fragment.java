package Fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import lesdevoreurs.bon_manger.DBHelper;
import lesdevoreurs.bon_manger.R;

/**
 * Fragment for the calendar
 */
public class Menu_Fragment extends Fragment {

    ArrayAdapter<CharSequence> adapter;
    View rootView;
    DatePicker datePicker;
    Button showButton;
    Button addButton;
    ListView listMenu;
    static SQLiteDatabase db;
    static DBHelper dbh;
    MyAdapter adapt;
    String d = "29042015";
    static Menu_Fragment instance;

    public Menu_Fragment() {
    }

    public static Menu_Fragment getInstance(){
        if (instance == null)
            instance = new Menu_Fragment();
        return instance;
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

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        dbh = new DBHelper(getActivity());
        db = dbh.getWritableDatabase();
        listMenu = (ListView) getView().findViewById(R.id.listMenu);

        //Get today's date and set it as default
        d = new SimpleDateFormat("ddMMyyyy").format(new Date());

        //Get ingredients info, and pass to adapter to fit in the listview
        final Cursor c2 = dbh.listMeals(db,d);

        //Create checklist with false
        adapt = new MyAdapter(getActivity(), c2);
        listMenu.setAdapter(adapter);
        super.onCreate(savedInstanceState);
        update(d);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //if the view doesn't exist yet, create it
        if (rootView == null)
        rootView = inflater.inflate(R.layout.menu_layout, container, false);

        //Init the objects assignation
        if (showButton == null)
        {
            datePicker = (DatePicker) rootView.findViewById((R.id.datePicker));
            showButton = (Button) rootView.findViewById((R.id.showDay));
            addButton = (Button) rootView.findViewById((R.id.addmeal));
            listMenu = (ListView) rootView.findViewById(R.id.listMenu);

            showButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    //Change the day
                    String day = String.format("%02d", datePicker.getDayOfMonth());
                    //Months returned by DatePickers start from 0 @January...
                    String month = String.format("%02d", datePicker.getMonth()+1);
                    String year = Integer.toString(datePicker.getYear());
                    d = day + month + year;
                    System.out.println("DATE IS NOW: " +d);
                    update(d);
                }
            });
            addButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Add a meal");
                    builder.setMessage("What meal do you want to eat?");
                    final EditText inputField = new EditText(getActivity());
                    builder.setView(inputField);
                    builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Cursor c3=DBHelper.searchMeal(db, inputField.getText().toString(), d);
                            if (c3.getCount()==0){
                                DBHelper.addMeal(db, inputField.getText().toString(), d);
                                update(d);
                            }
                        }
                    });

                    builder.setNegativeButton("Cancel", null);
                    builder.create().show();
                }

            });
        }

        return rootView;
    }

    private void update(String date) {
        final Cursor c2 = dbh.listMeals(db,date);
        adapt = new MyAdapter(getActivity(), c2);
        listMenu.setAdapter(adapt);
        adapt.notifyDataSetChanged();
    }
    public void del(String s, String date){
        DBHelper.deleteMeal(db, s, date);
        update(d);
    }
    public class MyAdapter extends CursorAdapter {

        LayoutInflater inflater;

        public MyAdapter(Context context, Cursor c) {
            super(context, c, true);
            inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View v = convertView;

            if (v == null) {
                v = inflater.inflate(R.layout.menu_meal_item, parent, false);
            }

            Button delButton = (Button) v.findViewById(R.id.delBtn);
            final TextView itemName = (TextView) v.findViewById(R.id.itemText);

            //Delete button listener + onClick
            delButton.setOnClickListener(new View.OnClickListener()
                                         {
                                             public void onClick(View v) {
                                                 Cursor c3=DBHelper.searchMeal(db, itemName.getText().toString(), d);
                                                 if (c3.getCount()!=0){
                                                     DBHelper.deleteMeal(db, itemName.getText().toString(), d);
                                                     update(d);
                                                 }
                                             }
                                         });

            //Get ingredients info
            Cursor c = getCursor();
            c.moveToPosition(position);
            String name = c.getString(c.getColumnIndex(DBHelper.CA_NAME));

            TextView titre = (TextView) v.findViewById(R.id.itemText);
            titre.setText(name);

            return v;
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {

            return null;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {

        }
    }
}
