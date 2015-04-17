package Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import lesdevoreurs.bon_manger.R;

/**
 * Created by virgile on 16/04/2015.
 */
public class Home_Fragment extends Fragment{

    public Home_Fragment(){};

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.home, container, false);

        return rootView;
    }


}
