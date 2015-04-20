package Fragments;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;

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
    TextView instructions;
    ListView ingredients;
    Button btIng;
    Button btIns;
    Button addBtn;
    ScrollView scrollIns;
    View view;

    //To pass to list
    public static ArrayList<String> nameIngredients = new ArrayList<String>();
    public static ArrayList<String> numberIngredients = new ArrayList<String>();
    public static boolean checkList[] = null;

    public RecetteAffiche_Fragment(){};

    //Constructor with the id of the recipe in parameters
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

        if(btIng==null) {
            idRecette = getArguments().getString("ID_RECETTE");
            titre = (TextView) getView().findViewById(R.id.titreR);
            description = (TextView) getView().findViewById(R.id.descR);
            temps = (TextView) getView().findViewById(R.id.ttR);
            cuisson = (TextView) getView().findViewById(R.id.tcR);
            instructions = (TextView) getView().findViewById(R.id.instR);
            ingredients = (ListView) getView().findViewById(R.id.ingreR);
            image = (ImageView) getView().findViewById(R.id.imgR);
            scrollIns = (ScrollView) getView().findViewById(R.id.scollR);

            //Show images, descriptions and temps when clicking on title
            titre.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ingredients.setVisibility(View.GONE);
                    btIng.setBackgroundColor(Color.GRAY);
                    scrollIns.setVisibility(View.GONE);
                    btIns.setBackgroundColor(Color.GRAY);
                    image.setVisibility(View.VISIBLE);
                    description.setVisibility(View.VISIBLE);
                    temps.setVisibility(View.VISIBLE);
                    cuisson.setVisibility(View.VISIBLE);
                }
            });

            //Show only title and ingredients
            btIng = (Button) getView().findViewById(R.id.ingR);
            btIng.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ingredients.setVisibility(View.VISIBLE);
                    btIng.setBackgroundColor(Color.DKGRAY);
                    scrollIns.setVisibility(View.GONE);
                    btIns.setBackgroundColor(Color.GRAY);
                    image.setVisibility(View.GONE);
                    description.setVisibility(View.GONE);
                    temps.setVisibility(View.GONE);
                    cuisson.setVisibility(View.GONE);
                }
            });

            //Show only title and instructions
            btIns = (Button) getView().findViewById(R.id.insR);
            btIns.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ingredients.setVisibility(View.GONE);
                    btIng.setBackgroundColor(Color.GRAY);
                    scrollIns.setVisibility(View.VISIBLE);
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
            new DownloadWebTask().execute();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //Reload the view in case of "back" or create a new one if it's the first time
        if (view == null)
            view = inflater.inflate(R.layout.research_recipe, container, false);
        return view;
    }

    //Search in BigOvenRecipeWebAPI
    public class DownloadWebTask extends AsyncTask<Void, Void, BigOvenRecipeWebAPI> {
        ProgressDialog progressDialog;
        boolean[] checkList;

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
            String instructionsR = bigovenrecipewebapi.instructions;
            Drawable cuisineR = bigovenrecipewebapi.image;
            final ArrayList<String> ingredientsN = bigovenrecipewebapi.ingredientsNom;
            final ArrayList<String> ingredientsNb = bigovenrecipewebapi.ingredientsQuantite;

            //Set text in UI
            titre.setText(titreR);
            image.setImageDrawable(cuisineR);
            description.setText(descriptionR);
            if(!tempsR.equals("0"))
                temps.setText("Ready in : "+tempsR);
            if(!cuissonR.equals("0"))
                cuisson.setText("Cooking time: "+cuissonR);
            btIns.setVisibility(View.VISIBLE);
            btIng.setVisibility(View.VISIBLE);
            instructions.setText(instructionsR);

            //Create checklist with false
            int size = ingredientsN.size();
            setCheckList(size);

            //Setup ingredient listview
            final MyAdapter adapter = new MyAdapter(ingredientsN, ingredientsNb);
            ingredients.setAdapter(adapter);
            progressDialog.dismiss();

            //The button to add the ingredients to the list
            addBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean[] checktemp = getCheckList();
                    String temp = ingredientsN.get(0);
                    if (!temp.equals("Nothing found")) {
                        for (int i = 0; i < checktemp.length; i++) {
                            if (checktemp[i]) {
                                getNameIngredients().add(ingredientsN.get(i));
                                getNumberIngredients().add(ingredientsNb.get(i));
                            }
                        }

                        //INSÉRER LE CODE POUR LIER AVEC LISTE RECETTE ICI, POUR L'INSTANT PRINT LA LISTE
                        for (int j = 0; j < getNameIngredients().size(); j++) {
                            Log.d("Liste", "" + getNameIngredients().get(j) + " "
                                    + getNumberIngredients().get(j));
                        }
                       resetNameIngredients();
                       resetNumberIngredients();
                    }
                }
            });
        }
    }

    public class MyAdapter extends BaseAdapter {

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
                    if(!checkList[position])
                        checkList[position] = true;
                    else
                        checkList[position] = false;
                }
            });

            //If nothing found
            if(nom.get(position).equals("Nothing found"))
                check.setTextIsSelectable(false);
            return v;
        }
    }

    //Create checklist with false
    public void setCheckList(int size){
        Log.d("Size",""+size);
        checkList = new boolean[size];
        for(int i=0; i<size; i++){
            checkList[i]=false;
            Log.d("Checklist",""+checkList[i]);
        }
    }

    public boolean[] getCheckList(){
        return checkList;
    }

    public ArrayList<String> getNameIngredients(){
        return nameIngredients;
    }

    public ArrayList<String> getNumberIngredients(){
        return numberIngredients;
    }

    public void resetNameIngredients(){
        nameIngredients = new ArrayList<String>();
    }

    public void resetNumberIngredients(){
        numberIngredients = new ArrayList<String>();
    }
}
