package Fragments;

import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import lesdevoreurs.bon_manger.DBHelper;
import lesdevoreurs.bon_manger.R;

/**
 * Created by virgile on 17/04/2015.
 */
public class CurrentRecipe_Fragment extends Fragment{

    ListView listI;
    MyAdapter adapter;
    DBHelper dbh;
    SQLiteDatabase db;

    public CurrentRecipe_Fragment(){};

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.current_recipe, container, false);

        return rootView;
    }

    public void onActivityCreated (final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Open DB
        dbh = new DBHelper(getActivity());
        db = dbh.getWritableDatabase();

        listI = (ListView)getView().findViewById(R.id.listI);

        //Entre des données test dans la db
        dbh.test(db);

        //Get recipe info
        Cursor c = dbh.currentRecipe(db);
        c.moveToPosition(0);
        String id = c.getString(c.getColumnIndex(DBHelper.R_ID));
        String t = c.getString(c.getColumnIndex(DBHelper.R_TITRE));
        String d = c.getString(c.getColumnIndex(DBHelper.R_DESCRIPTION));
        String i = c.getString(c.getColumnIndex(DBHelper.R_IMAGE));
        String ins = c.getString(c.getColumnIndex(DBHelper.R_INSTRUCTIONS));
        String tt = c.getString(c.getColumnIndex(DBHelper.R_TOTALTIME));
        String ct = c.getString(c.getColumnIndex(DBHelper.R_COOKTIME));

        //Get ingredients info, and pass to adapter to fit in the listview
        Cursor c2 = dbh.currentRecipeIngredients(db);
        adapter = new MyAdapter(getActivity(), c2);
        listI.setAdapter(adapter);

        //TextView titre = (TextView)v.findViewById(android.R.id.text1);
        //TextView description = (TextView)v.findViewById(android.R.id.text2);
        //titre.setText(t);
        //description.setText(d);
    }

    public class MyAdapter extends CursorAdapter {
        //ArrayList<String> titres;
        LayoutInflater inflater;

        public MyAdapter(Context context, Cursor c) {
            super(context, c, true);
            inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;

            if(v==null){
                v = inflater.inflate(R.layout.current_recipe_item, parent, false);
            }

            //Get ingredients info
            Cursor c = getCursor();
            c.moveToPosition(position);
            String id = c.getString(c.getColumnIndex(DBHelper.RI_ID));
            String name = c.getString(c.getColumnIndex(DBHelper.RI_NAME));
            String number = c.getString(c.getColumnIndex(DBHelper.RI_NUMBER));

            TextView titre = (TextView)v.findViewById(R.id.textCI);
            titre.setText(number+" "+name);

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

    public static void receiveRecipe(String titre, Drawable image, String description, String tempsCuisson, String tempsTotal,
                                     String instructions, ArrayList<String> ingreNom, ArrayList<String> ingreNum, String id){
        //We receive the informations of the recipe to add
        //Here we add it to memory
    }
}