package Fragments;

import android.app.Fragment;
import android.database.Cursor;
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
public class Menu_Fragment_PLACEHOLDER extends Fragment {
    public Menu_Fragment_PLACEHOLDER() {
    }

    ;

    public static void receiveRecipe(String titre, String image, String description, String tempsCuisson,
                                     String tempsTotal, String instructions, ArrayList<String> ingreNom,
                                     ArrayList<String> ingreNum, String id) {
        //We receive the informations of the recipe to add from search
        //Here we add it to memory
    }

    public static void receiveRecipe(String titre, String image, String description, String tempsCuisson,
                                     String tempsTotal, String instructions, Cursor c, String id) {
        //We receive the informations of the recipe to add from current recipe
        //Here we add it to memory
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.menu_layout_placeholder, container, false);

        return rootView;
    }
}
