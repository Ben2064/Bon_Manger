package Fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;

import lesdevoreurs.bon_manger.R;

/**
 * Created by virgile on 16/04/2015.
 */
public class Home_Fragment extends Fragment{

    View view;
    TextView title;
    TextView description;
    ImageView image;
    LinearLayout staff;

    public Home_Fragment(){};

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //Reload the view in case of "back" or create a new one if it's the first time
        if (view == null)
            view = inflater.inflate(R.layout.home, container, false);
        return view;
    }

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

        title.setText("Rosti");
        description.setText("Nicolas suggest you: this traditionnal Swiss casserole made with potato, turnip and eggs." +
                "A little taste of Europe in your plate");

        new DownloadImageTask("http://images.bigoven.com/image/upload/t_recipe-256/rosti-a.jpg").execute();
    }

    //Load and put image
    public class DownloadImageTask extends AsyncTask<Void, Void, Drawable> {

        String imagePath;
        Drawable imageDraw = null;

        public DownloadImageTask() {
        }

        public DownloadImageTask(String imagePath) {
            this.imagePath = imagePath;
        }

        @Override
        protected Drawable doInBackground(Void... params) {
            //Load image
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet http = new HttpGet(imagePath);
            HttpResponse response = null;
            try {
                response = httpClient.execute(http);
                InputStream is = response.getEntity().getContent();
                imageDraw = Drawable.createFromStream(is, "src");
            } catch (IOException e) {
                Log.d("Imageload", "Problème avec load d'image" + e);
            }
            return imageDraw;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(final Drawable imageDraw) {
            //ImageView image = (ImageView) getView().findViewById(R.id.imgC);
            image.setImageDrawable(imageDraw);
        }
    }
}
