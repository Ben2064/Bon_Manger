package Fragments;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import Fragments.ResearchRecipe_Fragment;
import lesdevoreurs.bon_manger.BigOvenWebAPI;
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
    //ArrayList<Drawable> image;
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
        DBHelper.addRecipe(db, id, titre, image, description,
                tempsCuisson, tempsTotal, instructions);

        //Add ingredients
        for (int i=0;i<ingreNom.size();i++) {
            DBHelper.addRecipeIngredient(db,ingreNom.get(i), ingreNum.get(i), ingreMet.get(i), id);
        }

        //db.delete("ringredients", null, null);
        //Add ingredients
        /*for (int i = 0; i < ingreNom.size(); i++) {
            DBHelper.addCurrentIngredient(db, ingreNom.get(i), ingreNum.get(i), i);
        }*/
    }

    public static void receiveRecipe(String titre, String image, String description, String tempsCuisson,
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


        if (spin == null) {
           // btnSearch = (Button) getView().findViewById(R.id.docherche);
           // btnEff = (Button) getView().findViewById(R.id.doefface);
           // edR = (EditText) getView().findViewById(R.id.cherche);
            spin = (Spinner) getView().findViewById((R.id.nbp));
            spin.setSelection(3);
            rate = (RatingBar) getView().findViewById(R.id.myRatingBar);
            listv = (ListView) getView().findViewById(R.id.activity_list);
            cookbook = (TextView) getView().findViewById(R.id.activity_title);
            cookbook.setText("CookBook");

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
            listv.addFooterView(btnLoad);

            //Load previous results
            btnBack = new Button(getActivity());
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
                            InputMethodManager.HIDE_NOT_ALWAYS);*/
                }
            });
            listv.addHeaderView(btnBack);
            btnBack.setVisibility(View.GONE);

            final Cursor cursor1= dbh.listRecipe(db);
            adapter = new MyAdapter(getActivity(),cursor1);
            listv.setAdapter(adapter);

           /* int nbrRecipes=cursor1.getCount();

            for (int i=0;i<nbrRecipes;i++)
            {

            }*/

            //Erase research text
            /*btnSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String recherche = edR.getText().toString();

                    //New search
                    rechPage = "";
                    btnLoad.setVisibility(View.GONE);
                    btnBack.setVisibility(View.GONE);

                    //If search field isn't empty, we perform the search
                    if (!recherche.matches("")) {
                        numPage = 1;
                        new DownloadWebTask().execute();

                        //Hide keyboard after hit the button
                        InputMethodManager inputManager = (InputMethodManager)
                                getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

                        inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                                InputMethodManager.HIDE_NOT_ALWAYS);
                    }
                }
            });*/

            //Erase the text in search field
            /*btnEff.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    edR.setText("");
                }
            });*/

        }
    }

    //Search in BigOvenWebAPI
   /* public class DownloadWebTask extends AsyncTask<Void, Void, BigOvenWebAPI> {

        String numByPage = "20";
        //To convert the images
        ArrayList<String> images;
        ArrayList<Drawable> drawImages;

        @Override
        protected BigOvenWebAPI doInBackground(Void... params) {

            String query = rechPage;

            //Make query
            if (rechPage == "") {  //If new search
                query = edR.getText().toString().replace(" ", "%20"); //Fill the space
                rechPage = query;
            } else    //If load more
                query = rechPage;
            numByPage = String.valueOf(spin.getSelectedItem());
            BigOvenWebAPI web = new BigOvenWebAPI(query, numPage, numByPage);

            //Preload the "noImage"
            String urlNoImage = "http://images.bigoven.com/image/upload/t_recipe-120/recipe-no-image.jpg";   //The "noImage" URL
            //String urlSexy = "http://dnok91peocsw3.cloudfront.net/product/68709-original-1366930648-primary.png";   //FUNFUNFUN
            Drawable noImage = null;
            try {
                //InputStream is = (InputStream) new URL(urlSexy).getContent(); //FUNFUNFUN
                InputStream is = (InputStream) new URL(urlNoImage).getContent();
                noImage = (Drawable.createFromStream(is, "src name"));
            } catch (IOException e) {
                Log.d("Inputstream", "Erreur noImage " + e);
            }

            //Load every images from website
            images = web.images;
            drawImages = new ArrayList<Drawable>();

            progressDialog.setMax(images.size()); //Put the number of pictures in progressbar

            //Check every images URL, to look for recipe without images
            for (int position = 0; position < images.size(); position++) {
                //If the recipe have no image
                if (images.get(position).equals(urlNoImage))
                    drawImages.add(noImage);
                    //If the recipe have an image
                else {
                    InputStream is2 = null;
                    try {
                        is2 = (InputStream) new URL(images.get(position)).getContent();
                    } catch (IOException e) {
                        Log.d("Inputstream", "Erreur Image " + e);
                    }
                    Drawable imageTemp = Drawable.createFromStream(is2, "src name");    //Load the image
                    //Check if there's a problem with the loaded images
                    if (imageTemp != null)
                        drawImages.add(imageTemp);
                    else
                        drawImages.add(noImage);
                }

                progressDialog.setProgress(position);   //Show loading progress
            }

            return web;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Window to see the progress of loading
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setTitle("Showing Recipes ...");
            progressDialog.setMessage("Loading. Please Wait");
            progressDialog.setIndeterminate(false);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setMax(50);
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(final BigOvenWebAPI bigovenwebapi) {

            //Get from BigOvenWebAPI
            final ArrayList<String> titres = bigovenwebapi.titres;
            ArrayList<String> cuisines = bigovenwebapi.cuisines;
            ArrayList<String> categories = bigovenwebapi.categories;
            ArrayList<String> sousCategories = bigovenwebapi.sousCategories;
            ArrayList<String> ratings = bigovenwebapi.ratings;

            //Showing the number of results. Here because it's not in the listview
            String nbResultats = bigovenwebapi.nbResultats;
            String nomResultats = "Results : ";
            if (nbResultats == "1" && nbResultats == "0")
                nomResultats = "Result : ";
            research.setText(nomResultats + nbResultats);

            String nbResult = research.getText().toString().replace("Result : ", "").replace("Results : ", "");
            if (nbResult != "Research") {
                double nbR = Double.parseDouble(nbResult);
                double nbRP = Double.parseDouble(numByPage);

                //Check if there's a next page
                if (Math.ceil(nbR / nbRP) >= numPage + 1)
                    btnLoad.setVisibility(View.VISIBLE);
                else
                    btnLoad.setVisibility(View.GONE);

                //Check if there's a previous apge
                if (numPage == 1)
                    btnBack.setVisibility(View.GONE);
                else
                    btnBack.setVisibility(View.VISIBLE);
            }

            final MyAdapter adapter = new MyAdapter(titres, cuisines, categories, sousCategories, drawImages, ratings, numByPage);
            listv.setAdapter(adapter);
            progressDialog.dismiss();   //End the progress window

            //Open recipe onclick
            listv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    boolean chercheRecette = true;
                    String titrecheck = titres.get(position - 1);

                    //Check if it's a recipe or a Nothing found before adding listener
                    if (titrecheck.length() >= 13) {
                        Log.d("Test", "" + titrecheck.substring(0, 13));
                        if (titrecheck.substring(0, 13).equals("Nothing found")) {
                            chercheRecette = false;
                        }
                    }
                    if (chercheRecette) {
                        String idRecette = bigovenwebapi.IDS.get(position - 1);
                        FragmentManager fragmentManager = getFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.frame_container, ResearchRecipe_Fragment.newInstance(
                                idRecette)).addToBackStack("rechercheBACK").commit();
                    }
                }
            });
        }
    }*/

/*    public class MyAdapter extends BaseAdapter {

        ArrayList<String> titres;
        ArrayList<String> cuisines;
        ArrayList<String> categories;
        ArrayList<String> sousCategories;
        ArrayList<Drawable> images;
        ArrayList<String> ratings;

        int numByPage;
        LayoutInflater inflater;

        public MyAdapter(ArrayList<String> titres, ArrayList<String> cuisines, ArrayList<String> categories, ArrayList<String> sousCategories,
                         ArrayList<Drawable> images, ArrayList<String> ratings, String numByPage) {
            inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            this.titres = titres;
            this.cuisines = cuisines;
            this.categories = categories;
            this.sousCategories = sousCategories;
            this.images = images;
            this.ratings = ratings;
            this.numByPage = Integer.parseInt(numByPage);
        }

        @Override
        public int getCount() {
            return titres.size();
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
        public View getView(int position, View convertView, ViewGroup parent) {

            View v = convertView;

            if (v == null) {
                v = inflater.inflate(R.layout.research_item, parent, false);
            }

            //Put everything in the listview
            TextView titre = (TextView) v.findViewById(R.id.nomRechRecette); //Title
            titre.setText(titres.get(position));
            TextView cuisine = (TextView) v.findViewById(R.id.cuisineRechRecette);   //Cuisine
            cuisine.setText(cuisines.get(position));
            TextView categorie = (TextView) v.findViewById(R.id.categorieRechRecette);   //Categorie
            categorie.setText(categories.get(position));
            TextView sousCategorie = (TextView) v.findViewById(R.id.subcategorieRechRecette);    //SubCategorie
            sousCategorie.setText(sousCategories.get(position));
            ImageView imageView = (ImageView) v.findViewById(R.id.imageRechRecette); //Image
            imageView.setImageDrawable(images.get(position));
            RatingBar rating = (RatingBar) v.findViewById(R.id.myRatingBar); //Rating
            if (ratings.get(position) != "") {   //If Rating is empty
                double star = Double.parseDouble(ratings.get(position));
                rating.setRating((int) star);
            } else
                rating.setVisibility(View.INVISIBLE);
            return v;
        }
    }*/
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
                v = inflater.inflate(R.layout.research_item, parent, false);
            }

            //Get Cookbook recipes
            Cursor c = getCursor();
            c.moveToPosition(position);
            String id = c.getString(c.getColumnIndex(DBHelper.C_ID));
            String title = c.getString(c.getColumnIndex(DBHelper.C_TITRE));
            String desc = c.getString(c.getColumnIndex(DBHelper.C_DESCRIPTION));
            String i = c.getString(c.getColumnIndex(DBHelper.C_IMAGE));
            String cookTime =c.getString(c.getColumnIndex(DBHelper.C_COOKTIME));
            String totalTime = c.getString(c.getColumnIndex(DBHelper.C_TOTALTIME));

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
            image = (ImageView) v.findViewById(R.id.imageRechRecette);

            DownloadImageTask imag=new DownloadImageTask(i);
            Drawable im2;

            new DownloadImageTask(i).execute();
            image.setImageDrawable(imageList.get(position));
            //imag.execute();
            //im2 = imag.getImage();
            //image.setImageDrawable(im2);


            /*
            //Store if checkbox are checked or not in the position of the ingredient
            check = (CheckBox) v.findViewById(R.id.checkCI); //Name
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
                check.setTextIsSelectable(false);*/

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
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(final Drawable imageDraw) {
            //ImageView image = (ImageView) getView().findViewById(R.id.imgC);
            //image.setImageDrawable(imageDraw);
            setImageList(imageDraw);
        }

        public Drawable getImage(){
            return imageDraw;
        }
    }

    public void setImageList(Drawable imagee) {
        this.imageList.add(imagee);
    }
}
