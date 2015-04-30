package Fragments;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import lesdevoreurs.bon_manger.BigOvenRecipeWebAPI;
import lesdevoreurs.bon_manger.DBHelper;
import lesdevoreurs.bon_manger.R;

/**
 * Recipe from research fragment
 * Created by Nicolas on 17/04/2015.
 */
public class ResearchRecipe_Fragment extends Fragment {

    //To pass to list
    public static ArrayList<String> nameIngredients = new ArrayList<String>();
    public static ArrayList<String> numberIngredients = new ArrayList<String>();
    public static ArrayList<String> metricIngredients = new ArrayList<String>();
    public static boolean checkList[] = null;
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
    Button btnMake;
    Button btnMenu;
    Button arrow;
    LinearLayout titreMore;

    View view;
    private String idRecette;   //The ID of the recipe to show

    /**
     * Default constructor
     */
    public ResearchRecipe_Fragment() {
    }

    /**
     * Constructor with the id of the recipe in parameters
     * @param id    The id of the recipe in the API
     * @return  Return fragment
     */
    public static ResearchRecipe_Fragment newInstance(String id) {

        ResearchRecipe_Fragment fragment = new ResearchRecipe_Fragment();

        Bundle args = new Bundle(1);
        args.putString("ID_RECETTE", id);
        fragment.setArguments(args);

        return fragment;
    }

    /**
     * Every time we open or reopen the fragment
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (btIng == null) {
            idRecette = getArguments().getString("ID_RECETTE");
            titre = (TextView) getView().findViewById(R.id.titreR);
            description = (TextView) getView().findViewById(R.id.descR);
            temps = (TextView) getView().findViewById(R.id.ttR);
            cuisson = (TextView) getView().findViewById(R.id.tcR);
            instructions = (TextView) getView().findViewById(R.id.instR);
            ingredients = (ListView) getView().findViewById(R.id.ingreR);
            image = (ImageView) getView().findViewById(R.id.imgR);
            titreMore = (LinearLayout) getView().findViewById(R.id.titreMore);
            arrow = (Button) getView().findViewById(R.id.arrow);

            //Show images, descriptions and temps when clicking on title
            titreMore.setOnClickListener(new View.OnClickListener() {
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

            //Start searching API
            final DownloadWebTask web = new DownloadWebTask();
            web.execute();

            //Add to cookbook
            btnFav = (Button) getView().findViewById(R.id.btnFav);
            final DBHelper dbh = new DBHelper(getActivity());
            final SQLiteDatabase db = dbh.getWritableDatabase();
            if(dbh.searchBookRecipe(db,idRecette).getCount()==0) {
                btnFav.setBackgroundResource(android.R.drawable.btn_star_big_off);
                /*btnFav.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Toast.makeText(getActivity(), "Added to my cookbook", Toast.LENGTH_LONG).show();
                        //Getting info
                        final String titre = web.getTitre();
                        final String imagePath = web.getImagePath();
                        final String description = web.getDesc();
                        final String tempsCuisson = web.getCuisson();
                        final String tempsTotal = web.getTemps();
                        final String instructions = web.getInstructions();
                        final ArrayList<String> ingreNom = web.getIname();
                        final ArrayList<String> ingreNum = web.getInumber();
                        final ArrayList<String> ingreMet = web.getImetric();
                        final String id = web.getID();
                        DBHelper dbh = new DBHelper(getActivity());
                        LivreListe_Fragment.receiveRecipe(dbh, titre, imagePath, description, tempsCuisson, tempsTotal,
                                instructions, ingreNom, ingreNum, ingreMet, id);
                        btnFav.setBackgroundResource(android.R.drawable.btn_star_big_on);
                    }
                });*/
            }
            else
                btnFav.setBackgroundResource(android.R.drawable.btn_star_big_on);

            btnFav.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                      if(dbh.searchBookRecipe(db,idRecette).getCount()==0) {

                          Toast.makeText(getActivity(), "Added to my cookbook", Toast.LENGTH_LONG).show();
                          //Getting info
                          final String titre = web.getTitre();
                          final String imagePath = web.getImagePath();
                          final String description = web.getDesc();
                          final String tempsCuisson = web.getCuisson();
                          final String tempsTotal = web.getTemps();
                          final String instructions = web.getInstructions();
                          final ArrayList<String> ingreNom = web.getIname();
                          final ArrayList<String> ingreNum = web.getInumber();
                          final ArrayList<String> ingreMet = web.getImetric();
                          final String id = web.getID();
                          DBHelper dbh = new DBHelper(getActivity());
                          LivreListe_Fragment.receiveRecipe(dbh, titre, imagePath, description, tempsCuisson, tempsTotal,
                                  instructions, ingreNom, ingreNum, ingreMet, id);
                          btnFav.setBackgroundResource(android.R.drawable.btn_star_big_on);
                      }else
                      {
                          Toast.makeText(getActivity(), "Removed from my cookbook", Toast.LENGTH_LONG).show();
                          final String id = web.getID();
                          DBHelper.deleteRecipe(db, id);
                          btnFav.setBackgroundResource(android.R.drawable.btn_star_big_off);
                      }
                  }
              });

            //Add to current recipe
            btnMake = (Button) getView().findViewById(R.id.btnMake);
            btnMake.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getActivity(), "Added to current recipe", Toast.LENGTH_LONG).show();
                    //Getting info
                    final String titre = web.getTitre();
                    final String imagePath = web.getImagePath();
                    final String description = web.getDesc();
                    final String tempsCuisson = web.getCuisson();
                    final String tempsTotal = web.getTemps();
                    final String instructions = web.getInstructions();
                    final ArrayList<String> ingreNom = web.getIname();
                    final ArrayList<String> ingreNum = web.getInumber();
                    final ArrayList<String> ingreMet = web.getImetric();
                    final String id = web.getID();

                    DBHelper dbh = new DBHelper(getActivity());
                    CurrentRecipe_Fragment.receiveRecipe(dbh, titre, imagePath, description, tempsCuisson, tempsTotal,
                            instructions, ingreNom, ingreNum, ingreMet, id);
                }
            });

            //Add to menu
            btnMenu = (Button) getView().findViewById(R.id.btnMenu);
            btnMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getActivity(), "Add to menu", Toast.LENGTH_LONG).show();
                    //Getting info
                    final String titre = web.getTitre();
                    final String imagePath = web.getImagePath();
                    final String description = web.getDesc();
                    final String tempsCuisson = web.getCuisson();
                    final String tempsTotal = web.getTemps();
                    final String instructions = web.getInstructions();
                    final ArrayList<String> ingreNom = web.getIname();
                    final ArrayList<String> ingreNum = web.getInumber();
                    final ArrayList<String> ingreMet = web.getImetric();
                    final String id = web.getID();
                    DBHelper dbh = new DBHelper(getActivity());
                    Menu_Fragment.receiveRecipe(dbh, titre, imagePath, description, tempsCuisson, tempsTotal,
                            instructions, ingreNom, ingreNum, ingreMet, id);
                }
            });
        }
    }

    /**
     * Everytime we open the view, reload if back, load new if from menu
     * @param inflater  The layout to use
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
     * Get checkList informations about ingredients
     * @return
     */
    public boolean[] getCheckList() {
        return checkList;
    }

    /**
     * Create checklist with false
     * @param size  Number of ingredients
     */
    public void setCheckList(int size) {
        Log.d("Size", "" + size);
        checkList = new boolean[size];
        for (int i = 0; i < size; i++) {
            checkList[i] = false;
            Log.d("Checklist", "" + checkList[i]);
        }
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
     * Search in BigOvenRecipeWebAPI
     */
    public class DownloadWebTask extends AsyncTask<Void, Void, BigOvenRecipeWebAPI> {
        ProgressDialog progressDialog;
        String ID;
        String titreR;
        String descriptionR;
        String tempsR;
        String cuissonR;
        String instructionsR;
        String imagePath;
        ArrayList<String> ingredientsN = new ArrayList<String>();
        ArrayList<String> ingredientsNb = new ArrayList<String>();
        ArrayList<String> ingredientsMet = new ArrayList<String>();
        boolean[] checkList;

        public DownloadWebTask() {
        }

        protected String getID() {
            return ID;
        }

        protected String getTitre() {
            return titreR;
        }

        protected String getDesc() {
            return descriptionR;
        }

        protected String getTemps() {
            return tempsR;
        }

        protected String getCuisson() {
            return cuissonR;
        }

        protected String getInstructions() {
            return instructionsR;
        }

        protected String getImagePath() {
            return imagePath;
        }

        protected ArrayList<String> getInumber() {
            return ingredientsNb;
        }

        protected ArrayList<String> getIname() {
            return ingredientsN;
        }

        protected ArrayList<String> getImetric() { return ingredientsMet; }

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
            titreR = bigovenrecipewebapi.titre;

            if(titreR!="OFFLINE") {
                descriptionR = bigovenrecipewebapi.description;
                tempsR = bigovenrecipewebapi.tempsTotal;
                cuissonR = bigovenrecipewebapi.tempsCuisson;
                instructionsR = bigovenrecipewebapi.instructions;
                imagePath = bigovenrecipewebapi.imagePath;
                ingredientsN = bigovenrecipewebapi.ingredientsNom;
                ingredientsNb = bigovenrecipewebapi.ingredientsQuantite;
                ingredientsMet = bigovenrecipewebapi.ingredientsMetric;
                ID = bigovenrecipewebapi.ID;

                //Set text in UI
                titre.setText(titreR);
                if (bigovenrecipewebapi != null)
                    Picasso.with(getActivity())
                            .load(imagePath)
                            .into(image);
                description.setText(descriptionR);
                if (!tempsR.equals("0"))
                    temps.setText("Ready in : " + tempsR + "min");
                if (!cuissonR.equals("0"))
                    cuisson.setText("Cooking time: " + cuissonR + "min");
                btIns.setVisibility(View.VISIBLE);
                btIng.setVisibility(View.VISIBLE);
                instructions.setText(instructionsR);

                //Create checklist with false
                int size = ingredientsN.size();
                setCheckList(size);

                //Setup ingredient listview
                final MyAdapter adapter = new MyAdapter(ingredientsN, ingredientsNb, ingredientsMet);
                ingredients.setAdapter(adapter);

                //The button to add the ingredients to the list
                addBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getActivity(), "Added to my list", Toast.LENGTH_LONG).show();
                        boolean[] checktemp = getCheckList();
                        String temp = ingredientsN.get(0);

                        //Store temporary
                        ArrayList<String> tempName = getNameIngredients();
                        ArrayList<String> tempNum = getNumberIngredients();
                        ArrayList<String> tempMet = getMetricIngredients();

                        if (!temp.equals("Nothing found")) {
                            for (int i = 0; i < checktemp.length; i++) {
                                if (checktemp[i]) {
                                    tempName.add(ingredientsN.get(i));
                                    tempNum.add(ingredientsNb.get(i));
                                    tempMet.add(ingredientsMet.get(i));
                                }
                            }

                            //INSÉRER LE CODE POUR LIER AVEC LISTE RECETTE ICI, POUR L'INSTANT PRINT LA LISTE
                            for (int j = 0; j < getNameIngredients().size(); j++) {
                                Log.d("Liste", "" + tempName.get(j) + " "
                                        + tempNum.get(j));
                            }
                            DBHelper dbh = new DBHelper(getActivity());
                            Liste_Fragment.setListe(dbh, tempName, tempNum, tempMet);
                            resetNameIngredients();
                            resetNumberIngredients();
                            resetMetricIngredients();
                        }
                    }
                });

                btnFav.setVisibility(View.VISIBLE);
                btnMake.setVisibility(View.VISIBLE);
                btnMenu.setVisibility(View.GONE);
            }
            else
                titre.setText("Cannot connect to internet...");
            progressDialog.dismiss();
        }
    }

    /**
     * Set everything in the listview of ingredients
     */
    public class MyAdapter extends BaseAdapter {

        ArrayList<String> nom;
        ArrayList<String> nombre;
        ArrayList<String> metric;
        LayoutInflater inflater;
        CheckBox check;

        public MyAdapter(ArrayList<String> n, ArrayList<String> nb, ArrayList<String> m) {
            inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            this.nom = n;
            this.nombre = nb;
            this.metric = m;
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
            String test = metric.get(position);
            titre.setText(nombre.get(position) + " " + metric.get(position) + " " + nom.get(position));

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
    }
}
