package Fragments;

import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import lesdevoreurs.bon_manger.DBHelper;
import lesdevoreurs.bon_manger.R;

/**
 * Created by virgile on 17/04/2015.
 */
public class CurrentRecipe_Fragment extends Fragment{

    static SQLiteDatabase db;
    //UI
    TextView titre;
    //ImageView image;
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
    View view;
    boolean[] checkList;
    ListView listI;
    MyAdapter adapter;
    DBHelper dbh;

    public CurrentRecipe_Fragment(){};

    public static void receiveRecipe(String titre, Drawable image, String description, String tempsCuisson, String tempsTotal,
                                     String instructions, ArrayList<String> ingreNom, ArrayList<String> ingreNum, String id) {
        //We receive the informations of the recipe to add
        //Here we add it to memory

        //DBHelper.test(db);

        //Add recipe
        byte[] imageDB = imageSQL(image);
        DBHelper.addCurrent(db, id, titre, imageDB, description,
                tempsCuisson, tempsTotal, instructions);

        db.delete("ringredients", null, null);
        //Add ingredients
        for (int i = 0; i < ingreNom.size(); i++) {
            DBHelper.addCurrentIngredient(db, ingreNom.get(i), ingreNum.get(i), i);
        }
    }

    //Convert drawable to byte[] for DB
    //http://stackoverflow.com/questions/6341977/convert-drawable-to-blob-datatype
    public static byte[] imageSQL(Drawable d) {
        BitmapDrawable bitDw = ((BitmapDrawable) d);
        Bitmap bitmap = bitDw.getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] imageInByte = stream.toByteArray();

        return imageInByte;
    }

    //Convert byte[] to bitmap
    public static Bitmap convertByteArrayToBitmap(byte[] byteImage) {
        Bitmap bitMapImage = BitmapFactory.decodeByteArray(
                byteImage, 0,
                byteImage.length);
        return bitMapImage;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //Reload the view in case of "back" or create a new one if it's the first time
        if (view == null)
            view = inflater.inflate(R.layout.current_recipe, container, false);
        return view;
    }

    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Open DB
        dbh = new DBHelper(getActivity());
        db = dbh.getWritableDatabase();

        listI = (ListView) getView().findViewById(R.id.ingreC);

        //Test d'entrée des données test dans la db
        dbh.test(db);

        //Get recipe info
        Cursor c = dbh.currentRecipe(db);
        c.moveToPosition(0);
        final String id = c.getString(c.getColumnIndex(DBHelper.R_ID));
        final String t = c.getString(c.getColumnIndex(DBHelper.R_TITRE));
        final String d = c.getString(c.getColumnIndex(DBHelper.R_DESCRIPTION));
        final byte[] i = c.getBlob(c.getColumnIndex(DBHelper.R_IMAGE));
        final String ins = c.getString(c.getColumnIndex(DBHelper.R_INSTRUCTIONS));
        final String tt = c.getString(c.getColumnIndex(DBHelper.R_TOTALTIME));
        final String ct = c.getString(c.getColumnIndex(DBHelper.R_COOKTIME));

        titre = (TextView) getView().findViewById(R.id.titreC);
        description = (TextView) getView().findViewById(R.id.descC);
        //image = (ImageView) getView().findViewById(R.id.imgC);
        instructions = (TextView) getView().findViewById(R.id.instC);
        temps = (TextView) getView().findViewById(R.id.ttC);
        cuisson = (TextView) getView().findViewById(R.id.tcC);
        ingredients = (ListView) getView().findViewById(R.id.ingreC);

        //Get ingredients info, and pass to adapter to fit in the listview
        final Cursor c2 = dbh.currentRecipeIngredients(db);
        //Create checklist with false
        int size = c2.getCount();
        setCheckList(size);
        adapter = new MyAdapter(getActivity(), c2);
        listI.setAdapter(adapter);

        //Show only title and ingredients
        btIng = (Button) getView().findViewById(R.id.ingC);
        btIng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ingredients.setVisibility(View.VISIBLE);
                btIng.setBackgroundColor(Color.DKGRAY);
                instructions.setVisibility(View.GONE);
                btIns.setBackgroundColor(Color.GRAY);
                //image.setVisibility(View.GONE);
                description.setVisibility(View.GONE);
                temps.setVisibility(View.GONE);
                cuisson.setVisibility(View.GONE);
            }
        });

        //Show only title and instructions
        btIns = (Button) getView().findViewById(R.id.insC);
        btIns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ingredients.setVisibility(View.GONE);
                btIng.setBackgroundColor(Color.GRAY);
                instructions.setVisibility(View.VISIBLE);
                btIns.setBackgroundColor(Color.DKGRAY);
                //image.setVisibility(View.GONE);
                description.setVisibility(View.GONE);
                temps.setVisibility(View.GONE);
                cuisson.setVisibility(View.GONE);
            }
        });

        addBtn = new Button(getActivity());
        addBtn.setText("Add to my list");
        addBtn.setBackgroundColor(Color.GRAY);
        ingredients.addFooterView(addBtn);

        //Add to cookbook
        /*btnFav = (Button) getView().findViewById(R.id.btnFav);
        btnFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Added to my list", Toast.LENGTH_LONG).show();
                //Livre_Fragment_PLACEHOLDER.receiveRecipe(t, i, d, ct, tt, ins, c2, id);
            }
        });

        //Add to menu
        btnMenu = (Button) getView().findViewById(R.id.btnMenu);
        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Add to menu", Toast.LENGTH_LONG).show();
                //Menu_Fragment_PLACEHOLDER.receiveRecipe(t, i, d, ct, tt,ins, c2, id);
            }
        });*/

        //Show images, descriptions and temps when clicking on title
        titre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ingredients.setVisibility(View.GONE);
                btIng.setBackgroundColor(Color.GRAY);
                instructions.setVisibility(View.GONE);
                btIns.setBackgroundColor(Color.GRAY);
                //image.setVisibility(View.VISIBLE);
                description.setVisibility(View.VISIBLE);
                temps.setVisibility(View.VISIBLE);
                cuisson.setVisibility(View.VISIBLE);
            }
        });

        //Put image
        Bitmap imageBit = convertByteArrayToBitmap(i);
        //image.setImageBitmap(imageBit);

        //Set text in UI
        titre.setText(t);
        description.setText(d);
        if (!tt.equals("0"))
            temps.setText("Ready in : " + tt);
        if (!ct.equals("0"))
            cuisson.setText("Cooking time: " + ct);
        btIns.setVisibility(View.VISIBLE);
        btIng.setVisibility(View.VISIBLE);
        instructions.setText(ins);
        //btnFav.setVisibility(View.VISIBLE);
        //btnMenu.setVisibility(View.VISIBLE);
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
                v = inflater.inflate(R.layout.current_recipe_item, parent, false);
            }

            //Get ingredients info
            Cursor c = getCursor();
            c.moveToPosition(position);
            String id = c.getString(c.getColumnIndex(DBHelper.RI_ID));
            String name = c.getString(c.getColumnIndex(DBHelper.RI_NAME));
            String number = c.getString(c.getColumnIndex(DBHelper.RI_NUMBER));

            TextView titre = (TextView) v.findViewById(R.id.textCI);
            titre.setText(number + " " + name);

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