package Fragments;

import android.app.Fragment;
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
public class Liste_Fragment_PLACEHOLDER extends Fragment {

    public Liste_Fragment_PLACEHOLDER(){};

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.liste_layout_placeholder, container, false);

        return rootView;
    }

    public static void setListe(ArrayList<String> tempName, ArrayList<String> tempNum){
        //We receive the informations of the ingredients list to add
        //Here we add it to memory
    }
}
