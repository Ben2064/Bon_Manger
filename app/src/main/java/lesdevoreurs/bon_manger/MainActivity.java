package lesdevoreurs.bon_manger;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


public class MainActivity extends ActionBarActivity implements View.OnClickListener {
    Button btn;
    Button btnEff;
    EditText edR;
    ListView listv;
    TextView titre;
    Context context = this;
    static ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recherche);

        btn = (Button)findViewById(R.id.docherche);
        btnEff = (Button)findViewById(R.id.doefface);
        edR = (EditText)findViewById(R.id.cherche);
        listv = (ListView)findViewById(R.id.activity_list);
        titre = (TextView)findViewById(R.id.activity_title);

        titre.setText("Recherche");
        btn.setOnClickListener(this);
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

    @Override
    public void onClick(View v) {
        String recherche = edR.getText().toString();
        if (!recherche.matches("")) {
            Toast.makeText(this, "Chargement des donnees du Web", Toast.LENGTH_SHORT).show();
            new DownloadWebTask().execute();
        }
    }

    public class DownloadWebTask extends AsyncTask<Void, Void, BigOvenWebAPI>{

        ArrayList<String> images;
        ArrayList<Drawable> drawImages;
        //Context context;

        @Override
        protected BigOvenWebAPI doInBackground(Void... params) {
            String query = edR.getText().toString().replace(" ", "%20");
            BigOvenWebAPI web = new BigOvenWebAPI(query);

            //preload pas d'image
            InputStream is = null;
            String urlNoImage = "http://images.bigoven.com/image/upload/recipe-no-image.jpg";
            Drawable noImage = null;
            try {
                is = (InputStream) new URL(urlNoImage).getContent();
                noImage = (Drawable.createFromStream(is, "src name"));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //Bitmap noImage = downloadBitmap(urlNoImage);

            images = web.images;
            drawImages = new ArrayList<Drawable>();
            progressDialog.setMax(images.size());
            for(int position=0; position<images.size(); position++) {   //images.size()
                if(images.get(position).equals(urlNoImage)) //si l'image est nulle
                    drawImages.add(noImage);
                else {  //image pas nulle
                    InputStream is2 = null;
                    try {
                        is2 = (InputStream) new URL(images.get(position).replace("http://redirect.bigoven.com/pics/rs/120/", "http://images.bigoven.com/image/upload/t_recipe-120/")).getContent();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //drawImages.add(Drawable.createFromStream(is2, "src name"));
                    Drawable imageTemp = Drawable.createFromStream(is2, "src name");
                    if(imageTemp!=null) //tout est beau
                        drawImages.add(imageTemp);
                    else    //si accent dans l'adresse
                        drawImages.add(noImage);
                }
                progressDialog.setProgress(position);
            }

            return web;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(context);
            progressDialog.setTitle("Downloading Recipes ...");
            progressDialog.setMessage("Loading. Please Wait");
            progressDialog.setIndeterminate(false);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            //progressDialog.setProgress(0);
            //progressDialog.setMax(20);
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(BigOvenWebAPI bigovenwebapi) {
            ArrayList<String> titres = bigovenwebapi.titres;
            ArrayList<String> cuisines = bigovenwebapi.cuisines;
            ArrayList<String> categories = bigovenwebapi.categories;
            ArrayList<String> sousCategories = bigovenwebapi.sousCategories;
            ArrayList<String> images = bigovenwebapi.images;

            MyAdapter adapter = new MyAdapter(titres, cuisines, categories, sousCategories, drawImages);
            listv.setAdapter(adapter);
            progressDialog.dismiss();

            int nbResultats = bigovenwebapi.nbResultats;
            String nomResultats = "Résultat : ";
            if(nbResultats > 0)
                nomResultats = "Résultats : ";
            titre.setText(nomResultats + nbResultats);


        }
    }

    public class MyAdapter extends BaseAdapter{
        ArrayList<String> titres;
        ArrayList<String> cuisines;
        ArrayList<String> categories;
        ArrayList<String> sousCategories;
        ArrayList<Drawable> images;

        LayoutInflater inflater;

        public MyAdapter(ArrayList<String> titres, ArrayList<String> cuisines, ArrayList<String> categories, ArrayList<String> sousCategories, ArrayList<Drawable> images){
            inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.titres = titres;
            this.cuisines = cuisines;
            this.categories = categories;
            this.sousCategories = sousCategories;
            this.images = images;
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

            TextView titre = (TextView)v.findViewById(R.id.nomRechRecette);
            titre.setText(titres.get(position));
            TextView cuisine = (TextView)v.findViewById(R.id.cuisineRechRecette);
            cuisine.setText(cuisines.get(position));
            TextView categorie = (TextView)v.findViewById(R.id.categorieRechRecette);
            categorie.setText(categories.get(position));
            TextView sousCategorie = (TextView)v.findViewById(R.id.subcategorieRechRecette);
            sousCategorie.setText(sousCategories.get(position));
            ImageView imageView = (ImageView)v.findViewById(R.id.imageRechRecette);
            //if(images.get(position)!=null)
            imageView.setImageDrawable(images.get(position));

            return v;
        }
    }

    //http://stackoverflow.com/questions/17120230/android-set-imageview-to-url
    private Bitmap downloadBitmap(String url) {
        Bitmap image = null;

        // initilize the default HTTP client object
        final DefaultHttpClient client = new DefaultHttpClient();

        //forming a HttoGet request
        final HttpGet getRequest = new HttpGet(url);
        try {

            HttpResponse response = client.execute(getRequest);

            //check 200 OK for success
            final int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode != HttpStatus.SC_OK) {
                Log.w("ImageDownloader", "Error " + statusCode +
                        " while retrieving bitmap from " + url);
                return null;

            }

            final HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream inputStream = null;
                try {
                    // getting contents from the stream
                    inputStream = entity.getContent();

                    // decoding stream data back into image Bitmap that android understands
                    image = BitmapFactory.decodeStream(inputStream);


                } finally {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    entity.consumeContent();
                }
            }
        } catch (Exception e) {
            // You Could provide a more explicit error message for IOException
            getRequest.abort();
            Log.e("ImageDownloader", "Something went wrong while" +
                    " retrieving bitmap from " + url + e.toString());
        }

        return image;
    }
}