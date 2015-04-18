package Fragments;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import lesdevoreurs.bon_manger.BigOvenRecipeWebAPI;
import lesdevoreurs.bon_manger.R;

/**
 * Created by virgile on 17/04/2015.
 */
public class RecetteAffiche_Fragment extends Fragment {

    private String idRecette;   //The ID of the recipe to show

    //UI
    TextView titre;
    ImageView image;
    TextView description;
    TextView temps;
    TextView cuisson;
    Button btIng;
    Button btIns;

    public RecetteAffiche_Fragment(){};

    public static RecetteAffiche_Fragment newInstance(String id){

        RecetteAffiche_Fragment fragment= new RecetteAffiche_Fragment();

        Bundle args = new Bundle(1);
        args.putString("ID_RECETTE", id);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onActivityCreated (final Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        idRecette = getArguments().getString("ID_RECETTE");
        titre = (TextView) getView().findViewById(R.id.titreR);
        description = (TextView)getView().findViewById(R.id.descR);
        temps = (TextView)getView().findViewById(R.id.ttR);
        cuisson = (TextView)getView().findViewById(R.id.tcR);
        image = (ImageView)getView().findViewById(R.id.imgR);
        btIng = (Button)getView().findViewById(R.id.ingR);
        btIns = (Button)getView().findViewById(R.id.insR);

        new DownloadWebTask().execute();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.research_recipe, container, false);

        return rootView;
    }

    //Search in BigOvenRecipeWebAPI
    public class DownloadWebTask extends AsyncTask<Void, Void, BigOvenRecipeWebAPI> {
        ProgressDialog progressDialog;

        @Override
        protected BigOvenRecipeWebAPI doInBackground(Void... params) {
            //Load API of the recipe
            BigOvenRecipeWebAPI web = new BigOvenRecipeWebAPI(idRecette);
            return web;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setTitle("Showing Recipe ...");
            progressDialog.setMessage("Loading. Please Wait");
            progressDialog.setIndeterminate(false);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(final BigOvenRecipeWebAPI bigovenrecipewebapi) {

            //Get from BigOvenRecipeWebAPI
            String titreR = bigovenrecipewebapi.titre;
            String descriptionR = bigovenrecipewebapi.description;
            String tempsR = bigovenrecipewebapi.tempsTotal;
            String cuissonR = bigovenrecipewebapi.tempsCuisson;
            Drawable cuisineR = bigovenrecipewebapi.image;

            titre.setText(titreR);
            image.setImageDrawable(cuisineR);
            description.setText(descriptionR);
            if(!tempsR.equals("0"))
                temps.setText("Ready in : "+tempsR);
            if(!cuissonR.equals("0"))
                cuisson.setText("Cooking time: "+cuissonR);
            btIns.setVisibility(View.VISIBLE);
            btIng.setVisibility(View.VISIBLE);
            progressDialog.dismiss();
        }
    }
}
