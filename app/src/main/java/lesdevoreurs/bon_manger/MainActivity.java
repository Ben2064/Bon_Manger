package lesdevoreurs.bon_manger;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity implements View.OnClickListener {
    Button btn;
    EditText edR;
    ListView listv;
    TextView titre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recherche);

        btn = (Button)findViewById(R.id.docherche);
        edR = (EditText)findViewById(R.id.cherche);
        listv = (ListView)findViewById(R.id.activity_list);
        titre = (TextView)findViewById(R.id.activity_title);

        titre.setText("Recherche de recettes");
        btn.setOnClickListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        String recherche = edR.getText().toString();
        if (!recherche.matches("")) {
            Toast.makeText(this, "Chargement des donnees du Web", Toast.LENGTH_SHORT).show();
            new DownloadWebTask().execute();
        }
    }

    public class DownloadWebTask extends AsyncTask<Void, Void, BigOvenWebAPI>{

        @Override
        protected BigOvenWebAPI doInBackground(Void... params) {
            String query = edR.getText().toString().replace(" ", "%20");
            BigOvenWebAPI web = new BigOvenWebAPI(query);
            return web;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(BigOvenWebAPI bigovenwebapi) {
            ArrayList<String> titres = bigovenwebapi.titres;
            MyAdapter adapter = new MyAdapter(titres);
            listv.setAdapter(adapter);
        }
    }

    public class MyAdapter extends BaseAdapter{
        ArrayList<String> titres;
        LayoutInflater inflater;

        public MyAdapter(ArrayList<String> titres){
            inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.titres= titres;
        }

        @Override
        public int getCount() {
            return titres.size();
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
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;

            if(v==null){
                v = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
            }

            TextView titre = (TextView)v.findViewById(android.R.id.text1);
            titre.setText(titres.get(position));

            return v;
        }


    }
}
