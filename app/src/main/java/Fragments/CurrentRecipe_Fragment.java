package Fragments;

import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import lesdevoreurs.bon_manger.DBHelper;
import lesdevoreurs.bon_manger.R;

/**
 * Fragment for current recipe
 * Created by Nicolas on 2015-04-12.
 */
public class CurrentRecipe_Fragment extends Fragment{

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
    Button btnFav;
    Button btnMenu;
    Button arrow;
    View view;

    //To pass to list
    public static ArrayList<String> nameIngredients = new ArrayList<String>();
    public static ArrayList<String> numberIngredients = new ArrayList<String>();
    public static ArrayList<String> metricIngredients = new ArrayList<String>();
    boolean[] checkList;

    /**
     * Default constructor
     */
    public CurrentRecipe_Fragment() {
    }

    /**
     * We receive the informations of the recipe to add
     * Here we add it to memory
     * @param dbh   The Database
     * @param titre Title of the recipe
     * @param imagePath Path of the picture
     * @param description   Description fo the recipe
     * @param tempsCuisson  Cooktime of recipe
     * @param tempsTotal    Total time of preparation of recipe
     * @param instructions  Instructions of recipes
     * @param ingreNom  Names of ingredients
     * @param ingreNum  Number of ingredients
     * @param ingrMet   Metric use to the number of ingredient
     * @param id    Id of the recipe
     */
    public static void receiveRecipe(DBHelper dbh, String titre, String imagePath, String description, String tempsCuisson, String tempsTotal,
                                     String instructions, ArrayList<String> ingreNom, ArrayList<String> ingreNum,
                                     ArrayList<String> ingrMet, String id) {

        //Open DB
        db = dbh.getWritableDatabase();

        //Add and replace recipe
        DBHelper.addCurrent(db, id, titre, imagePath, description,
                tempsCuisson, tempsTotal, instructions);

        //Add and replace ingredients
        db.delete("ringredients", null, null);
        for (int i = 0; i < ingreNom.size(); i++) {
            DBHelper.addCurrentIngredient(db, ingreNom.get(i), ingreNum.get(i), ingrMet.get(i), i);
        }
    }

    /**
     * When we open the fragment by the menu or by hitting the back button
     * Reload if back, or create new instance if it's by menu
     * @param inflater  Use the research_recipe layout, beacause it's the same look
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Reload the view in case of "back" or create a new one if it's the first time
        if (view == null)
            view = inflater.inflate(R.layout.research_recipe, container, false);
        return view;
    }

    /**
     * Load each time we open the fragment
     * @param savedInstanceState
     */
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Open DB
        dbh = new DBHelper(getActivity());
        db = dbh.getWritableDatabase();

        //Check if no current recipe
        titre = (TextView) getView().findViewById(R.id.titreR);
        Cursor c = dbh.currentRecipe(db);
        if(c.getCount()==0)
            titre.setText("No current recipe!");

        //Don't reload everything if we hit back button
        if (!dbh.isEmpty(db) && addBtn == null) {

            //Get recipe info from DB
            c.moveToPosition(0);
            final String id = c.getString(c.getColumnIndex(DBHelper.R_ID));
            final String t = c.getString(c.getColumnIndex(DBHelper.R_TITRE));
            final String i = c.getString(c.getColumnIndex(DBHelper.R_IMAGE));
            final String d = c.getString(c.getColumnIndex(DBHelper.R_DESCRIPTION));
            final String ins = c.getString(c.getColumnIndex(DBHelper.R_INSTRUCTIONS));
            final String tt = c.getString(c.getColumnIndex(DBHelper.R_TOTALTIME));
            final String ct = c.getString(c.getColumnIndex(DBHelper.R_COOKTIME));

            //Get UI
            description = (TextView) getView().findViewById(R.id.descR);
            image = (ImageView) getView().findViewById(R.id.imgR);
            instructions = (TextView) getView().findViewById(R.id.instR);
            temps = (TextView) getView().findViewById(R.id.ttR);
            cuisson = (TextView) getView().findViewById(R.id.tcR);
            ingredients = (ListView) getView().findViewById(R.id.ingreR);
            arrow = (Button)getView().findViewById(R.id.arrow);

            //Get ingredients info, and pass to adapter to fit in the listview
            final Cursor c2 = dbh.currentRecipeIngredients(db);

            //Create checklist with false
            int size = c2.getCount();
            setCheckList(size);
            adapter = new MyAdapter(getActivity(), c2);
            ingredients.setAdapter(adapter);

            //Show only title and ingredients
            btIng = (Button) getView().findViewById(R.id.ingR);
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
                    arrow.setVisibility(View.VISIBLE);
                }
            });

            //Show only title and instructions
            btIns = (Button) getView().findViewById(R.id.insR);
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
                    arrow.setVisibility(View.VISIBLE);
                }
            });

            addBtn = new Button(getActivity());
            addBtn.setText("Add to my list");
            addBtn.setBackgroundColor(Color.GRAY);
            ingredients.addFooterView(addBtn);

            //Add to cookbook
            btnFav = (Button) getView().findViewById(R.id.btnFav);
            if(dbh.searchBookRecipe(db,id).getCount()==0) {
                btnFav.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getActivity(), "Added to my cookbook", Toast.LENGTH_LONG).show();
                        LivreListe_Fragment.receiveRecipe(dbh, t, i, d, ct, tt, ins, c2, id);
                        btnFav.setBackgroundResource(android.R.drawable.btn_star_big_on);
                    }
                });
            }
            else
                btnFav.setBackgroundResource(android.R.drawable.btn_star_big_on);

            //Add to menu
            btnMenu = (Button) getView().findViewById(R.id.btnMenu);
            btnMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getActivity(), "Add to menu", Toast.LENGTH_LONG).show();
                    Menu_Fragment_PLACEHOLDER.receiveRecipe(dbh, t, i, d, ct, tt, ins, c2, id);
                }
            });

            //The button to add the ingredients to the list
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

                        //INSÉRER LE CODE POUR LIER AVEC LISTE RECETTE ICI, POUR L'INSTANT PRINT LA LISTE
                        for (int j = 0; j < getNameIngredients().size(); j++) {
                            Log.d("Liste", "" + tempName.get(j) + " "
                                    + tempNum.get(j));
                        }
                        DBHelper dbh = new DBHelper(getActivity());
                        Liste_Fragment_PLACEHOLDER.setListe(dbh, tempName, tempNum, tempMet);
                        resetNameIngredients();
                        resetNumberIngredients();
                        resetMetricIngredients();
                    }
                }
            });

            //Show images, descriptions and times when clicking on title
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
                    arrow.setVisibility(View.GONE);
                }
            });

            //Set text in UI
            titre.setText(t);
            description.setText(d);
            if (!tt.equals("0"))
                temps.setText("Ready in : " + tt + "min");
            if (!ct.equals("0"))
                cuisson.setText("Cooking time: " + ct + "min");
            btIns.setVisibility(View.VISIBLE);
            btIng.setVisibility(View.VISIBLE);
            instructions.setText(ins);
            btnFav.setVisibility(View.VISIBLE);
            btnMenu.setVisibility(View.VISIBLE);

            Picasso.with(getActivity())
                    .load(i)
                    .into(image);
        }
    }

    /**
     * Create checklist with false
     * @param size  Number of ingredients
     */
    public void setCheckList(int size) {
        checkList = new boolean[size];
        for (int i = 0; i < size; i++) {
            checkList[i] = false;
        }
    }

    /**
     * Get checkList informations about ingredients
     * @return
     */
    public boolean[] getCheckList() {
        return checkList;
    }

    /**
     * Get list of ingredients name
     * @return  List
     */
    public ArrayList<String> getNameIngredients() {
        return nameIngredients;
    }

    /**
     * Get list of how many ingredients
     * @return  List
     */
    public ArrayList<String> getNumberIngredients() {
        return numberIngredients;
    }

    /**
     * Get list of wich metric it's in use with the ingredients
     * @return  List
     */
    public ArrayList<String> getMetricIngredients() { return metricIngredients; }

    /**
     * Erease the list of ingredients name when we put a new current recipe
     */
    public void resetNameIngredients() {
        nameIngredients = new ArrayList<String>();
    }

    /**
     * Erease the list of number of ingredients when we put a new current recipe
     */
    public void resetNumberIngredients() {
        numberIngredients = new ArrayList<String>();
    }

    /**
     * Erease the list of metrics uses by number of ingredients when we put a new current recipe
     */
    public void resetMetricIngredients() { metricIngredients = new ArrayList<String>();}

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