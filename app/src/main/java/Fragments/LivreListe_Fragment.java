package Fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import lesdevoreurs.bon_manger.DBHelper;
import lesdevoreurs.bon_manger.R;

/**
 * Created by virgile on 22/04/2015.
 */
public class LivreListe_Fragment extends Fragment {
    static ProgressDialog progressDialog;
    static int numPage;
    static String rechPage = "";
    static SQLiteDatabase db;
    static DBHelper dbh;


    //UI elements
    //Button btnSearch;
    //Button btnEff;
    Button btnLoad;
    Button btnBack;
    //EditText edR;
    ListView listv;
    RatingBar rate;
    Spinner spin;
    TextView cookbook;
    View view;
    ImageView image;
    ArrayList<String> titres;
    ArrayList<String> cuisines;
    ArrayList<String> categories;
    ArrayList<String> sousCategories;
    ArrayList<String> ratings;
    ArrayList<Drawable> imageList = new ArrayList<Drawable>();
    MyAdapter adapter;

    public LivreListe_Fragment(){}

    public static void receiveRecipe(DBHelper dbh, String titre, String image, String description, String tempsCuisson,
                                     String tempsTotal, String instructions, ArrayList<String> ingreNom,
                                     ArrayList<String> ingreNum, ArrayList<String> ingreMet, String id) {
        //We receive the informations of the recipe to add from search
        //Here we add it to memory
        //Open DB
        db = dbh.getWritableDatabase();

        //Add recipe
        //byte[] imageDB = imageSQL(image);
        Log.d("Fromrecherche",image);
        if(DBHelper.searchBookRecipe(db,id).getCount() == 0) {
            DBHelper.addRecipe(db, id, titre, image, description,
                    tempsCuisson, tempsTotal, instructions);
        }

        //Add ingredients
        for (int i=0;i<ingreNom.size();i++) {
            DBHelper.addRecipeIngredient(db,ingreNom.get(i), ingreNum.get(i), ingreMet.get(i), id);
        }
    }

    public static void receiveRecipe(DBHelper dbh, String titre, String image, String description, String tempsCuisson,
                                     String tempsTotal, String instructions, Cursor c, String id) {
        //We receive the informations of the recipe to add from current recipe
        //Here we add it to memory
        //Open DB
        db = dbh.getWritableDatabase();

        //Add recipe
        //byte[] imageDB = imageSQL(image);
        DBHelper.addRecipe(db, id, titre, image, description,
                tempsCuisson, tempsTotal, instructions);

        int numIngredients = c.getCount();
        String nom;
        String num;
        String met;
        //Add ingredient
        for (int i=0;i<numIngredients;i++) {
            c.moveToPosition(i);
            nom = c.getString(c.getColumnIndex(DBHelper.RI_NAME));
            num = c.getString(c.getColumnIndex(DBHelper.RI_NUMBER));
            met = c.getString(c.getColumnIndex(DBHelper.RI_METRIC));
            dbh.addRecipeIngredient(db,nom,num,met,id);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Reload the view in case of "back" or create a new one if it's the first time
        if (view == null)
            view = inflater.inflate(R.layout.livre_layout, container, false);
        return view;
    }


    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //open db
        dbh = new DBHelper(getActivity());
        db = dbh.getWritableDatabase();

        //Check if no favorite recipe
        cookbook = (TextView) getView().findViewById(R.id.activity_title);
        Cursor cursor1 = dbh.listRecipe(db);
        if(cursor1.getCount()==0)
            cookbook.setText("The cookbook is empty!");
        else
            cookbook.setText("CookBook");

        if (spin == null) {
            // btnSearch = (Button) getView().findViewById(R.id.docherche);
            // btnEff = (Button) getView().findViewById(R.id.doefface);
            // edR = (EditText) getView().findViewById(R.id.cherche);
            spin = (Spinner) getView().findViewById((R.id.nbp));
            spin.setSelection(3);
            spin.setVisibility(view.GONE);
            rate = (RatingBar) getView().findViewById(R.id.myRatingBar);
            listv = (ListView) getView().findViewById(R.id.activity_list);


            //Load another page of result
            btnLoad = new Button(getActivity());
            btnLoad.setText("Next Page");
            btnLoad.setTextColor(Color.LTGRAY);
            btnLoad.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {

                  /*  // Starting new async task
                    numPage++;
                    new DownloadWebTask().execute();

                    //Hide keyboard after hit the button
                    InputMethodManager inputManager = (InputMethodManager)
                            getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

                    inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);*/
                }
            });
            //listv.addFooterView(btnLoad);

            //Load previous results
           /* btnBack = new Button(getActivity());
            btnBack.setText("Previous Page");
            btnBack.setTextColor(Color.LTGRAY);
            btnBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {

                    // Starting new async task
                    numPage--;
                    /*new DownloadWebTask().execute();

                    //Hide keyboard after hit the button
                    InputMethodManager inputManager = (InputMethodManager)
                            getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

                    inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                }
            });
            listv.addHeaderView(btnBack);
            btnBack.setVisibility(View.GONE);*/

            adapter = new MyAdapter(getActivity(), cursor1);
            listv.setAdapter(adapter);

            //Open recipe onclick
            listv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                    View slcRecette = listv.getChildAt(position);
                    TextView iRecette = (TextView)slcRecette.findViewById(R.id.categorieRechRecette);
                    String idRecette = iRecette.getText().toString();
                    System.out.println("ss"+idRecette);
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.frame_container, LivreRecette_Fragment.newInstance(
                            idRecette)).addToBackStack("rechercheBACK").commit();
                }

            });
        }
    }

    public class MyAdapter extends CursorAdapter {

        LayoutInflater inflater;
        ArrayList<Drawable> images;

        public MyAdapter(Context context, Cursor c) {
            super(context, c, true);
            inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View v = convertView;

            if (v == null) {
                v = inflater.inflate(R.layout.research_item, parent, false);
            }

            //Get Cookbook recipes
            Cursor c = getCursor();
            c.moveToPosition(position);
            String id = c.getString(c.getColumnIndex(DBHelper.C_ID));
            String title = c.getString(c.getColumnIndex(DBHelper.C_TITRE));
            String desc = c.getString(c.getColumnIndex(DBHelper.C_DESCRIPTION));
            String cookTime =c.getString(c.getColumnIndex(DBHelper.C_COOKTIME));
            String totalTime = c.getString(c.getColumnIndex(DBHelper.C_TOTALTIME));
            String imagePath = c.getString(c.getColumnIndex(DBHelper.C_IMAGE));

            TextView titre = (TextView) v.findViewById(R.id.nomRechRecette);
            titre.setText(title);
            TextView ct = (TextView) v.findViewById(R.id.cuisineRechRecette);
            ct.setText("Cook: "+cookTime+" min");
           // ct.setWidth(120);
            ct.setLayoutParams(new TableLayout.LayoutParams(60,60,5));
            TextView tt = (TextView) v.findViewById(R.id.subcategorieRechRecette);
            tt.setText("Total: "+totalTime + " min");
            tt.setLayoutParams(new TableLayout.LayoutParams(60,60,5));
            TextView st = (TextView) v.findViewById(R.id.categorieRechRecette);
            st.setVisibility(View.GONE);
            st.setText(id);
            ImageView image = (ImageView) v.findViewById(R.id.imageRechRecette);
            Picasso.with(getActivity())
                    .load(imagePath)
                    .into(image);
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
