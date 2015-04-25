package Fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import lesdevoreurs.bon_manger.R;

/**
 * Fragment for homepage
 * Created by Virgile and Nicolas on 16/04/2015.
 */
public class Home_Fragment extends Fragment{

    //**********RECIPE OF THE MONTH!!! TEXT TO CHANGE EACH MONTH**********
    String nameM = "Rosti";
    String descriptionM = "Nicolas suggests you: this traditionnal Swiss casserole made with potato, turnip and eggs." +
            "A little taste of Europe in your plate";
    String imageM = "http://images.bigoven.com/image/upload/t_recipe-256/rosti-a.jpg";
    //**********RECIPE OF THE MONTH!!! TEXT TO CHANGE EACH MONTH**********

    View view;
    TextView title;
    TextView description;
    ImageView image;
    LinearLayout staff;

    /**
     * Default constructor
     */
    public Home_Fragment(){};

    /**
     * When we open the fragment by the menu or by hitting the back button
     * Reload if back, or create new instance if it's by menu
     * @param inflater  Use the research_recipe layout, beacause it's the same look
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //Reload the view in case of "back" or create a new one if it's the first time
        if (view == null)
            view = inflater.inflate(R.layout.home, container, false);
        return view;
    }

    /**
     * Load each time we open the fragment
     * @param savedInstanceState
     */
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        title = (TextView) getView().findViewById(R.id.staffTitle);
        description = (TextView) getView().findViewById(R.id.staffDesc);
        image = (ImageView) getView().findViewById(R.id.staffImg);
        staff = (LinearLayout) getView().findViewById(R.id.staff);
        final String id = "205609";

        staff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.frame_container, ResearchRecipe_Fragment.newInstance(
                        id)).addToBackStack("homeBACK").commit();
            }
        });

        //**********RECIPE OF THE MONTH!!! TEXT TO CHANGE EACH MONTH**********
        title.setText(nameM);
        description.setText(descriptionM);
        Picasso.with(getActivity())
                .load(imageM)
                .into(image);
        //*********RECIPE OF THE MONTH!!! TEXT TO CHANGE EACH MONTH*********

    }
}
