package Fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import lesdevoreurs.bon_manger.DBHelper;
import lesdevoreurs.bon_manger.R;

/**
 * Created by virgile on 17/04/2015.
 */
public class Liste_Fragment_PLACEHOLDER extends Fragment {

    static SQLiteDatabase db;
    static DBHelper dbh;
    static Liste_Fragment_PLACEHOLDER instance;
    Cursor cu;
    ListView listI;
    MyAdapter adapter;
    View view;
    ArrayList<String> ings = new ArrayList<>();

    public Liste_Fragment_PLACEHOLDER() {
    }

    ;

    public static void setListe(DBHelper dbhelp, ArrayList<String> tempName, ArrayList<String> tempNum, ArrayList<String> tempMet) {
        //We receive the informations of the ingredients list to add
        //Here we add it to memory
        db=dbhelp.getWritableDatabase();
        for (int i=0;i<tempName.size();i++){
            Cursor c3=DBHelper.searchIngredient(db,tempName.get(i));
            if (c3.getCount()==0)
                dbhelp.addIngredient(db,tempName.get(i),tempNum.get(i),tempMet.get(i));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (view == null)
            view = inflater.inflate(R.layout.liste_layout_placeholder, container, false);
        Log.d("a", "createview");
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        //Open DB
        dbh = new DBHelper(getActivity());
        db = dbh.getWritableDatabase();
        cu = DBHelper.listIngredients(db);
        listI = (ListView) getView().findViewById(R.id.list);
        //Get ingredients info, and pass to adapter to fit in the listview
        final Cursor c2 = dbh.listIngredients(db);
        //Create checklist with false
        adapter = new MyAdapter(getActivity(), c2);
        listI.setAdapter(adapter);
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        update();
    }

    public static Liste_Fragment_PLACEHOLDER getInstance(){
        if (instance==null)
            instance= new Liste_Fragment_PLACEHOLDER();
        return instance;
    }
    public void add(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add an ingredient");
        builder.setMessage("What do you need to buy?");
        final EditText inputField = new EditText(getActivity());
        builder.setView(inputField);
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Cursor c3=DBHelper.searchIngredient(db,inputField.getText().toString());
                if (c3.getCount()==0){
                    DBHelper.addIngredient(db, inputField.getText().toString(), "1", " ");
                    update();
                }
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.create().show();
    }
    public void del(){
        for (int i=0;i<ings.size();i++){
            DBHelper.deleteIngredient(db,ings.get(i));
        }
        ings=new ArrayList<String>();
        update();
    }

    public void quantity(String texte){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Quantity");
        builder.setMessage("Set a numeric value for quantity");
        final EditText inputField = new EditText(getActivity());
        builder.setView(inputField);
        final String t = texte;
        builder.setPositiveButton("Set", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                try {
                    Integer.parseInt(inputField.getText().toString());
                } catch(NumberFormatException e) {
                    Toast.makeText(getActivity().getApplicationContext(), "Enter a numeric value",
                            Toast.LENGTH_SHORT).show();
                    return;
                } catch(NullPointerException e) {
                    return;
                }
                Log.d("nouvelle qt",inputField.getText().toString());
                DBHelper.setIngredientsNumber(db, t, inputField.getText().toString());
                update();
                }

        });

        builder.setNegativeButton("Cancel", null);
        builder.create().show();
        update();
    }
    public void metrique(String texte){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Unit");
        builder.setMessage("Set a type of unit");
        final EditText inputField = new EditText(getActivity());
        builder.setView(inputField);
        final String t = texte;
        builder.setPositiveButton("Set", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                DBHelper.setIngredientsMetric(db, t, inputField.getText().toString());
                update();
            }

        });

        builder.setNegativeButton("Cancel", null);
        builder.create().show();
        update();
    }

    public void check(String texte){
        boolean found = false;
       for (int i=0;i<ings.size();i++){
           if (ings.get(i).equals(texte)){
               found = true;
               Log.d("aaaa",texte);
               ings.remove(i);
           }
       }
       if (found==false){
           ings.add(texte);
       }
        Log.d("rrrr",String.valueOf(ings.size()));
    }

    private void update() {
        cu.moveToFirst();
        /*Log.d("MainActivity cursor",
                cu.getString(0));
        while(cu.moveToNext()) {
            Log.d("MainActivity cursor ",
                    cu.getString(0));
        }*/
        //listI = (ListView) getView().findViewById(R.id.list);
        //Get ingredients info, and pass to adapter to fit in the listview
        final Cursor c2 = dbh.listIngredients(db);
        //Create checklist with false
        adapter = new MyAdapter(getActivity(), c2);
        listI.setAdapter(adapter);
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
                v = inflater.inflate(R.layout.ingredient, parent, false);
            }
            //Get ingredients info
            Cursor c = getCursor();
            c.moveToPosition(position);
            String metrique = c.getString(c.getColumnIndex(DBHelper.RI_METRIC));
            String name = c.getString(c.getColumnIndex(DBHelper.RI_NAME));
            String number = c.getString(c.getColumnIndex(DBHelper.RI_NUMBER));

            TextView titre = (TextView) v.findViewById(R.id.ingTextView);
            Button qt = (Button) v.findViewById(R.id.quantity);
            qt.setText(number);
            Button met = (Button) v.findViewById(R.id.metButton);
            met.setText(metrique);
            CheckBox box = (CheckBox) v.findViewById(R.id.checkBox);
            //box.setBackgroundColor(000000);
            titre.setText("- "+name);
            titre.setTextColor(Color.WHITE);

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
