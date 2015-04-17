package Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import lesdevoreurs.bon_manger.R;

/**
 * Created by virgile on 17/04/2015.
 */
public class RecetteAffiche_Fragment extends Fragment {

    private String idRecette;

    public RecetteAffiche_Fragment(){};



    public static RecetteAffiche_Fragment newInstance(String id){

        RecetteAffiche_Fragment fragment= new RecetteAffiche_Fragment();

        Bundle args = new Bundle(1);
        args.putString("ID_RECETTE", id);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        idRecette = getArguments().getString("ID_RECETTE");
        Toast.makeText(getActivity().getBaseContext(), idRecette, Toast.LENGTH_LONG).show();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.research_pager_fragment, container, false);

        return rootView;
    }

}
