package lesdevoreurs.bon_manger;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;


public class RechercheActivity extends ActionBarActivity implements View.OnClickListener {

    //UI elements
    Button btnSearch;
    Button btnEff;
    Button btnLoad;
    EditText edR;
    ListView listv;
    RatingBar rate;
    Spinner spin;
    TextView titre;

    Context context = this;
    static ProgressDialog progressDialog;
    static int numPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recherche);

        btnSearch = (Button)findViewById(R.id.docherche);
        btnEff = (Button)findViewById(R.id.doefface);
        edR = (EditText)findViewById(R.id.cherche);
        spin = (Spinner)findViewById((R.id.nbp));
        spin.setSelection(3);
        rate = (RatingBar)findViewById(R.id.myRatingBar);
        listv = (ListView)findViewById(R.id.activity_list);
        titre = (TextView)findViewById(R.id.activity_title);
        titre.setText("Research");

        //Load another page of result
        btnLoad = new Button(this);
        btnLoad.setText("Load More");
        listv.addFooterView(btnLoad);
        btnLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // Starting new async task
                numPage++;
                new DownloadWebTask().execute();
            }
        });

        //Erase research text
        btnSearch.setOnClickListener(this);
        btnEff.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                edR.setText("");
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //Perform search
    @Override
    public void onClick(View v) {

        String recherche = edR.getText().toString();

        //If search field isn't empty, we perform the search
        if (!recherche.matches("")) {
            Toast.makeText(this, "Chargement des donnees du Web", Toast.LENGTH_SHORT).show();
            numPage = 1;
            new DownloadWebTask().execute();
        }

        //Hide keyboard after hit the button
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    //Search in BigOvenWebAPI
    public class DownloadWebTask extends AsyncTask<Void, Void, BigOvenWebAPI>{

        //To convert the images
        ArrayList<String> images;
        ArrayList<Drawable> drawImages;

        @Override
        protected BigOvenWebAPI doInBackground(Void... params) {

            //Make query
            String query = edR.getText().toString().replace(" ", "%20"); //Fill the space
            String numByPage = String.valueOf(spin.getSelectedItem());
            BigOvenWebAPI web = new BigOvenWebAPI(query, numPage, numByPage);

            //Preload the "noImage"
            String urlNoImage = "http://images.bigoven.com/image/upload/recipe-no-image.jpg";   //The "noImage" URL
            Drawable noImage = null;
            try {
                InputStream is = (InputStream) new URL(urlNoImage).getContent();
                noImage = (Drawable.createFromStream(is, "src name"));
            } catch (IOException e) {
                Log.d("Inputstream", "Erreur noImage "+e);
            }

            //Load every images from website
            images = web.images;
            drawImages = new ArrayList<Drawable>();

            progressDialog.setMax(images.size()); //Put the number of pictures in progressbar

            //Check every images URL, to look for recipe without images
            for(int position=0; position<images.size(); position++) {
                //If the recipe have no image
                if(images.get(position).equals(urlNoImage))
                    drawImages.add(noImage);
                    //If the recipe have an image
                else {
                    InputStream is2 = null;
                    try {
                        is2 = (InputStream) new URL(images.get(position).replace("http://redirect.bigoven.com/pics/rs/120/", "http://images.bigoven.com/image/upload/t_recipe-120/")).getContent();
                    } catch (IOException e) {
                        Log.d("Inputstream", "Erreur Image "+e);
                    }
                    Drawable imageTemp = Drawable.createFromStream(is2, "src name");    //Load the image
                    //Check if there's a problem with the loaded images
                    if(imageTemp!=null)
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
            progressDialog = new ProgressDialog(context);
            progressDialog.setTitle("Showing Recipes ...");
            progressDialog.setMessage("Loading. Please Wait");
            progressDialog.setIndeterminate(false);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setMax(50);
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(BigOvenWebAPI bigovenwebapi) {

            //Get from BigOvenWebAPI
            ArrayList<String> titres = bigovenwebapi.titres;
            ArrayList<String> cuisines = bigovenwebapi.cuisines;
            ArrayList<String> categories = bigovenwebapi.categories;
            ArrayList<String> sousCategories = bigovenwebapi.sousCategories;
            ArrayList<String> ratings = bigovenwebapi.ratings;

            MyAdapter adapter = new MyAdapter(titres, cuisines, categories, sousCategories, drawImages, ratings);
            listv.setAdapter(adapter);
            progressDialog.dismiss();   //End the progress window

            //Showing the number of results. Here because it's not in the listview
            String nbResultats = bigovenwebapi.nbResultats;
            String nomResultats = "Results : ";
            if(nbResultats == "1" && nbResultats == "0")
                nomResultats = "Result : ";
            titre.setText(nomResultats + nbResultats);
        }
    }

    public class MyAdapter extends BaseAdapter{

        ArrayList<String> titres;
        ArrayList<String> cuisines;
        ArrayList<String> categories;
        ArrayList<String> sousCategories;
        ArrayList<Drawable> images;
        ArrayList<String> ratings;

        LayoutInflater inflater;

        public MyAdapter(ArrayList<String> titres, ArrayList<String> cuisines, ArrayList<String> categories, ArrayList<String> sousCategories,
                         ArrayList<Drawable> images, ArrayList<String> ratings){
            inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.titres = titres;
            this.cuisines = cuisines;
            this.categories = categories;
            this.sousCategories = sousCategories;
            this.images = images;
            this.ratings = ratings;
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

            if(v==null){
                v = inflater.inflate(R.layout.element_recherche, parent, false);
            }

            //Put everything in the listview
            TextView titre = (TextView)v.findViewById(R.id.nomRechRecette); //Title
            titre.setText(titres.get(position));
            TextView cuisine = (TextView)v.findViewById(R.id.cuisineRechRecette);   //Cuisine
            cuisine.setText(cuisines.get(position));
            TextView categorie = (TextView)v.findViewById(R.id.categorieRechRecette);   //Categorie
            categorie.setText(categories.get(position));
            TextView sousCategorie = (TextView)v.findViewById(R.id.subcategorieRechRecette);    //SubCategorie
            sousCategorie.setText(sousCategories.get(position));
            ImageView imageView = (ImageView)v.findViewById(R.id.imageRechRecette); //Image
            imageView.setImageDrawable(images.get(position));
            RatingBar rating = (RatingBar)v.findViewById(R.id.myRatingBar); //Rating
            double star = Double.parseDouble(ratings.get(position));
            rating.setRating((int)star);

            return v;
        }
    }
}