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
 * Created by virgile on 22/04/2015.
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

            Cursor c2 = dbh.searchBookRecipeIngredients(db, idRecette);

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
            //btIng = (Button) getView().findViewById(R.id.ingR);
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
            //btIns = (Button) getView().findViewById(R.id.insR);
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

            //Start searching API
           /* final DownloadWebTask web = new DownloadWebTask();
            web.execute();*/

            //Remove from cookbook
            //btnDelete = (Button) getView().findViewById(R.id.btnFav);
            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dbh.searchBookRecipe(db,id).getCount()==0)
                    {
                        btnDelete.setVisibility(view.GONE);
                    }else
                    {
                        DBHelper.deleteRecipe(db,idRecette);
                        btnDelete.setVisibility(view.GONE);
                        Toast.makeText(getActivity(), "Deleted from my cookbook", Toast.LENGTH_LONG).show();
                    }

                    //Getting info
                   /* final String titre = web.getTitre();
                    final String imagePath = web.getImagePath();
                    final String description = web.getDesc();
                    final String tempsCuisson = web.getCuisson();
                    final String tempsTotal = web.getTemps();
                    final String instructions = web.getInstructions();
                    final ArrayList<String> ingreNom = web.getIname();
                    final ArrayList<String> ingreNum = web.getInumber();
                    final String id = web.getID();
                    Livre_Fragment_PLACEHOLDER.receiveRecipe(titre, imagePath, description, tempsCuisson, tempsTotal,
                            instructions, ingreNom, ingreNum, id);*/


                }
            });

            //Add to current recipe
            //btnMake = (Button) getView().findViewById(R.id.btnMake);
            btnMake.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getActivity(), "Added to current recipe", Toast.LENGTH_LONG).show();

                    //Getting info
                   /* final String titre = web.getTitre();
                    final String imagePath = web.getImagePath();
                    final String description = web.getDesc();
                    final String tempsCuisson = web.getCuisson();
                    final String tempsTotal = web.getTemps();
                    final String instructions = web.getInstructions();
                    final ArrayList<String> ingreNom = web.getIname();
                    final ArrayList<String> ingreNum = web.getInumber();
                    final String id = web.getID();
                    DBHelper dbh = new DBHelper(getActivity());
                    CurrentRecipe_Fragment.receiveRecipe(dbh, titre, imagePath, description, tempsCuisson, tempsTotal,
                            instructions, ingreNom, ingreNum, id);*/

                    DBHelper dbh = new DBHelper(getActivity());
                    //DBHelper dbh = new DBHelper(getActivity());
                    CurrentRecipe_Fragment.receiveRecipe(dbh, t, i, d, ct, tt,
                            ins, nameIngredients, numberIngredients, metricIngredients, id);

                    //CurrentRecipe_Fragment.receiveRecipe(dbh,t,i,d,ct,tt,ins,;
                }
            });

            //Add to menu
            //btnMenu = (Button) getView().findViewById(R.id.btnMenu);
            btnMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getActivity(), "Add to menu", Toast.LENGTH_LONG).show();
                    //Getting info
                    /*final String titre = web.getTitre();
                    final String imagePath = web.getImagePath();
                    final String description = web.getDesc();
                    final String tempsCuisson = web.getCuisson();
                    final String tempsTotal = web.getTemps();
                    final String instructions = web.getInstructions();
                    final ArrayList<String> ingreNom = web.getIname();
                    final ArrayList<String> ingreNum = web.getInumber();
                    final String id = web.getID();
                    Menu_Fragment_PLACEHOLDER.receiveRecipe(titre, imagePath, description, tempsCuisson, tempsTotal,
                            instructions, ingreNom, ingreNum, id);*/
                    dbh = new DBHelper(getActivity());
                    db = dbh.getWritableDatabase();
                    //DBHelper.deleteRecipe(db,id,i,d,ct,);
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

    public void resetNameIngredients() {
        nameIngredients = new ArrayList<String>();
    }

    public void resetNumberIngredients() {
        numberIngredients = new ArrayList<String>();
    }

 /*   public class MyAdapter extends BaseAdapter {

        ArrayList<String> nom;
        ArrayList<String> nombre;
        LayoutInflater inflater;
        CheckBox check;

        public MyAdapter(ArrayList<String> n, ArrayList<String> nb) {
            inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            this.nom = n;
            this.nombre = nb;
        }

        @Override
        public int getCount() {
            return nom.size();
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
        public View getView(final int position, View convertView, ViewGroup parent) {

            View v = convertView;

            if (v == null) {
                v = inflater.inflate(R.layout.research_recipe_item, parent, false);
            }

            //Put everything in the listview
            TextView titre = (TextView) v.findViewById(R.id.textI); //Name
            titre.setText(nombre.get(position) + " " + nom.get(position));

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
            if (nom.get(position).equals("Nothing found"))
                check.setTextIsSelectable(false);

            return v;
        }
    }*/

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


        //Load and put image
    public class DownloadImageTask extends AsyncTask<Void, Void, Drawable> {

        String imagePath;
        Drawable imageDraw = null;

        public DownloadImageTask() {
        }

        public DownloadImageTask(String imagePath) {
            this.imagePath = imagePath;
        }

        @Override
        protected Drawable doInBackground(Void... params) {
            //Load image
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet http = new HttpGet(imagePath);
            HttpResponse response = null;
            try {
                response = httpClient.execute(http);
                InputStream is = response.getEntity().getContent();
                imageDraw = Drawable.createFromStream(is, "src");
            } catch (IOException e) {
                Log.d("Imageload", "Problème avec load d'image" + e);
            }
            return imageDraw;
        }
    }
}

