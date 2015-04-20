package Fragments;

import android.app.Fragment;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import lesdevoreurs.bon_manger.R;

/**
 * Created by virgile on 17/04/2015.
 */
public class Menu_Fragment_PLACEHOLDER extends Fragment{
    public Menu_Fragment_PLACEHOLDER(){};

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.menu_layout_placeholder, container, false);

        return rootView;
    }

    public static void receiveRecipe(String titre, Drawable image, String description, String tempsCuisson, String tempsTotal, String instructions, ArrayList<String> ingreNom, ArrayList<String> ingreNum) {
        //We receive the informations of the recipe to add
        //Here we add it to memory
    }
}
