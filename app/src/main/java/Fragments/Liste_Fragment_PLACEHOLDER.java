package Fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import lesdevoreurs.bon_manger.DBHelper;
import lesdevoreurs.bon_manger.MainActivity;
import lesdevoreurs.bon_manger.R;

/**
 * Created by virgile on 17/04/2015.
 */
public class Liste_Fragment_PLACEHOLDER extends Fragment {

    static SQLiteDatabase db;
    static DBHelper dbh;
    Cursor cu;
    ListView listI;
    MyAdapter adapter;
    View view;

    public Liste_Fragment_PLACEHOLDER(){};

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (view == null)
            view  = inflater.inflate(R.layout.liste_layout_placeholder, container, false);
        Log.d("a","createview");
        return view;
    }

    public static void setListe(ArrayList<String> tempName, ArrayList<String> tempNum){
        //We receive the informations of the ingredients list to add
        //Here we add it to memory
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        //Open DB
        dbh = new DBHelper(getActivity());
        db = dbh.getWritableDatabase();
        cu = DBHelper.listIngredients(db);
        update();
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.add_ingredient, menu);
        super.onCreateOptionsMenu(menu, inflater);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_ingredient:
                Log.d("MainActivity", "Add an ingredient");
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Add an ingredient");
                builder.setMessage("What do you need to buy?");
                final EditText inputField = new EditText(getActivity());
                builder.setView(inputField);
                builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.d("MainActivity",inputField.getText().toString());
                        DBHelper.addIngredient(db,inputField.getText().toString(),"1");
                    }
                });

                builder.setNegativeButton("Cancel",null);
                builder.create().show();
                return true;

            default:
                return false;
        }
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
                v = inflater.inflate(R.layout.liste_layout_placeholder, parent, false);
            }
            v = inflater.inflate(R.layout.liste_layout_placeholder, parent, false);
            //Get ingredients info
            Cursor c = getCursor();
            c.moveToPosition(position);
            String id = c.getString(c.getColumnIndex(DBHelper.RI_ID));
            String name = c.getString(c.getColumnIndex(DBHelper.RI_NAME));
            String number = c.getString(c.getColumnIndex(DBHelper.RI_NUMBER));

            TextView titre = (TextView) v.findViewById(R.id.textCI);
            titre.setText(number + " " + name);

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

    private void update(){
        cu.moveToFirst();
        Log.d("MainActivity cursor",
                cu.getString(0));
        while(cu.moveToNext()) {
            Log.d("MainActivity cursor ",
                    cu.getString(0));
        }
        listI = (ListView) getView();
        //Get ingredients info, and pass to adapter to fit in the listview
        final Cursor c2 = dbh.listIngredients(db);
        //Create checklist with false
        adapter = new MyAdapter(getActivity(), c2);
        listI.setAdapter(adapter);
    }

}
