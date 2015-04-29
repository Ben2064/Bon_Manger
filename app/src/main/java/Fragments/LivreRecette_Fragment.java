package Fragments;

import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import lesdevoreurs.bon_manger.DBHelper;
import lesdevoreurs.bon_manger.R;

/**
 * Fragment to show recipe when selected in the cookbook. Can add items to the grocery list, add to menu(*) and remove from
 * cookbook
 */

public class LivreRecette_Fragment extends Fragment{

    static SQLiteDatabase db;
    static DBHelper dbh;
    MyAdapter adapter;


    //UI
    TextView titre;
    ImageView image;
    TextView description;
    TextView temps;
    TextView cuisson;
    TextView instructions;
    ListView ingredients;
    Button btIng;
    Button btIns;
    Button addBtn;
    Button btnDelete;
    Button btnMake;
    Button btnMenu;
    View view;
    private String idRecette;   //The ID of the recipe to show

    //To pass to list
    public static ArrayList<String> nameIngredients = new ArrayList<String>();
    public static ArrayList<String> numberIngredients = new ArrayList<String>();
    public static ArrayList<String> metricIngredients = new ArrayList<String>();
    boolean[] checkList;

    public LivreRecette_Fragment() {
    }

    //Constructor with the id of the recipe in parameters
    public static LivreRecette_Fragment newInstance(String id) {

        LivreRecette_Fragment fragment = new LivreRecette_Fragment();

        Bundle args = new Bundle(1);
        args.putString("ID_RECETTE", id);
        fragment.setArguments(args);

        return fragment;
    }


    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbh = new DBHelper(getActivity());
        db=dbh.getWritableDatabase();

        if (btIng == null) {

            idRecette = getArguments().getString("ID_RECETTE");
            System.out.println(idRecette);
            Cursor c = dbh.searchBookRecipe(db, idRecette);
            c.moveToPosition(0);
            final String id = c.getString(c.getColumnIndex(DBHelper.R_ID));
            final String t = c.getString(c.getColumnIndex(DBHelper.R_TITRE));
            final String i = c.getString(c.getColumnIndex(DBHelper.R_IMAGE));
            final String d = c.getString(c.getColumnIndex(DBHelper.R_DESCRIPTION));
            final String ins = c.getString(c.getColumnIndex(DBHelper.R_INSTRUCTIONS));
            final String tt = c.getString(c.getColumnIndex(DBHelper.R_TOTALTIME));
            final String ct = c.getString(c.getColumnIndex(DBHelper.R_COOKTIME));

            final Cursor c2 = dbh.searchBookRecipeIngredients(db, idRecette);

            int size = c2.getCount();
            setCheckList(size);
            adapter = new MyAdapter(getActivity(), c2);

            //new DownloadImageTask(i).execute();

            titre = (TextView) getView().findViewById(R.id.titreL);
            description = (TextView) getView().findViewById(R.id.descL);
            temps = (TextView) getView().findViewById(R.id.ttL);
            cuisson = (TextView) getView().findViewById(R.id.tcL);
            instructions = (TextView) getView().findViewById(R.id.instL);
            ingredients = (ListView) getView().findViewById(R.id.ingreL);
            image = (ImageView) getView().findViewById(R.id.imgL);
            btIng = (Button) getView().findViewById(R.id.ingL);
            btIns = (Button) getView().findViewById(R.id.insL);
            btnMenu = (Button) getView().findViewById(R.id.btnMenu);
            btnDelete = (Button) getView().findViewById(R.id.btnRemove);
            btnMake = (Button) getView().findViewById(R.id.btnMake);

            ingredients.setAdapter(adapter);

            titre.setText(t);
            description.setText(d);
            if (!tt.equals("0"))
                temps.setText("Ready in : " + tt);
            if (!ct.equals("0"))
                cuisson.setText("Cooking time: " + ct);
            btIns.setVisibility(View.VISIBLE);
            btIng.setVisibility(View.VISIBLE);
            instructions.setText(ins);
            btnDelete.setVisibility(View.VISIBLE);
            btnMenu.setVisibility(View.VISIBLE);
            btnMake.setVisibility(View.VISIBLE);

            Picasso.with(getActivity())
                    .load(i)
                    .into(image);

            //Show images, descriptions and temps when clicking on title
            titre.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ingredients.setVisibility(View.GONE);
                    btIng.setBackgroundColor(Color.GRAY);
                    instructions.setVisibility(View.GONE);
                    btIns.setBackgroundColor(Color.GRAY);
                    image.setVisibility(View.VISIBLE);
                    description.setVisibility(View.VISIBLE);
                    temps.setVisibility(View.VISIBLE);
                    cuisson.setVisibility(View.VISIBLE);
                }
            });

            //Show only title and ingredients
            btIng.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ingredients.setVisibility(View.VISIBLE);
                    btIng.setBackgroundColor(Color.DKGRAY);
                    instructions.setVisibility(View.GONE);
                    btIns.setBackgroundColor(Color.GRAY);
                    image.setVisibility(View.GONE);
                    description.setVisibility(View.GONE);
                    temps.setVisibility(View.GONE);
                    cuisson.setVisibility(View.GONE);
                }
            });

            //Show only title and instructions

            btIns.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ingredients.setVisibility(View.GONE);
                    btIng.setBackgroundColor(Color.GRAY);
                    instructions.setVisibility(View.VISIBLE);
                    btIns.setBackgroundColor(Color.DKGRAY);
                    image.setVisibility(View.GONE);
                    description.setVisibility(View.GONE);
                    temps.setVisibility(View.GONE);
                    cuisson.setVisibility(View.GONE);
                }
            });

            addBtn = new Button(getActivity());
            addBtn.setText("Add to my list");
            addBtn.setBackgroundColor(Color.GRAY);
            ingredients.addFooterView(addBtn);

            //Adding items to grocery list
            addBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getActivity(), "Added to my list", Toast.LENGTH_LONG).show();
                    boolean[] checktemp = getCheckList();
                    c2.moveToPosition(0);
                    String tempNom = c2.getString(c2.getColumnIndex(DBHelper.CI_NAME));

                    //Store temporary
                    ArrayList<String> tempName = getNameIngredients();
                    ArrayList<String> tempNum = getNumberIngredients();
                    ArrayList<String> tempMet = getMetricIngredients();

                    if (!tempNom.equals("Nothing found")) {
                        for (int i = 0; i < c2.getCount(); i++) {
                            if (checktemp[i]) {
                                c2.moveToPosition(i);
                                tempName.add(c2.getString(c2.getColumnIndex(DBHelper.CI_NAME)));
                                tempNum.add(c2.getString(c2.getColumnIndex(DBHelper.CI_NUMBER)));
                                tempMet.add(c2.getString(c2.getColumnIndex(DBHelper.CI_METRIC)));
                            }
                        }

                        DBHelper dbh = new DBHelper(getActivity());
                        Liste_Fragment_PLACEHOLDER.setListe(dbh, tempName, tempNum, tempMet); //send to grocerylist
                    }
                }
            });


            //Remove from cookbook
            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dbh.searchBookRecipe(db,id).getCount()==0)  //just to make sure
                    {
                        btnDelete.setVisibility(view.GONE);
                    }else
                    {
                        DBHelper.deleteRecipe(db,idRecette);
                        btnDelete.setVisibility(view.GONE);
                        Toast.makeText(getActivity(), "Deleted from my cookbook", Toast.LENGTH_LONG).show();
                    }

                }
            });

            //Add to current recipe
            btnMake.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getActivity(), "Added to current recipe", Toast.LENGTH_LONG).show();


                    DBHelper dbh = new DBHelper(getActivity());

                    CurrentRecipe_Fragment.receiveRecipe(dbh, t, i, d, ct, tt,
                            ins, nameIngredients, numberIngredients, metricIngredients, id);
                }
            });

            //Add to menu, not implemented yet (deluxe version only!)
            btnMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getActivity(), "Add to menu", Toast.LENGTH_LONG).show();
                    dbh = new DBHelper(getActivity());
                    db = dbh.getWritableDatabase();
                }
            });
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //Reload the view in case of "back" or create a new one if it's the first time
        if (view == null)
            view = inflater.inflate(R.layout.livre_recipe, container, false);
        return view;
    }

    public boolean[] getCheckList() {
        return checkList;
    }

    //Create checklist with false
    public void setCheckList(int size) {
        Log.d("Size", "" + size);
        checkList = new boolean[size];
        for (int i = 0; i < size; i++) {
            checkList[i] = false;
            Log.d("Checklist", "" + checkList[i]);
        }
    }

    public ArrayList<String> getNameIngredients() {
        return nameIngredients;
    }

    public ArrayList<String> getNumberIngredients() {
        return numberIngredients;
    }

    public ArrayList<String> getMetricIngredients() { return metricIngredients; }


    /**
     * Put everything in listview
     */
    public class MyAdapter extends CursorAdapter {

        LayoutInflater inflater;
        CheckBox check;

        public MyAdapter(Context context, Cursor c) {
            super(context, c, true);
            inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View v = convertView;

            if (v == null) {
                v = inflater.inflate(R.layout.research_recipe_item, parent, false);
            }

            //Get ingredients info
            Cursor c = getCursor();
            c.moveToPosition(position);
            String id = c.getString(c.getColumnIndex(DBHelper.RI_ID));
            String name = c.getString(c.getColumnIndex(DBHelper.RI_NAME));
            String number = c.getString(c.getColumnIndex(DBHelper.RI_NUMBER));
            String metric = c.getString(c.getColumnIndex(DBHelper.RI_METRIC));
            nameIngredients.add(name);
            numberIngredients.add(number);
            metricIngredients.add(metric);

            //Put ingredients info
            TextView titre = (TextView) v.findViewById(R.id.textI);
            titre.setText(number + " " + metric + " " + name);

            //Store if checkbox are checked or not in the position of the ingredient
            check = (CheckBox) v.findViewById(R.id.checkI); //Name
            check.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    if (!checkList[position])
                        checkList[position] = true;
                    else
                        checkList[position] = false;
                }
            });

            //If nothing found
            if (name.equals("Nothing found"))
                check.setTextIsSelectable(false);

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

