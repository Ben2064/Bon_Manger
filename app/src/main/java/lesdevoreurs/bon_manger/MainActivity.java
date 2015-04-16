package lesdevoreurs.bon_manger;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity implements View.OnClickListener {

    Button cherche;
    Button recette;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        myAdapter adapter;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        cherche = (Button)findViewById(R.id.gogogirl);
        recette = (Button)findViewById(R.id.recette);

        cherche.setOnClickListener(this);
        recette.setOnClickListener(this);
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
        if(v.getId()==R.id.gogogirl) {
            Toast.makeText(this, "Switching to Search Activity!", Toast.LENGTH_LONG).show();
            Intent i = new Intent(this, RechercheActivity.class);
            startActivity(i);
        }
        if(v.getId()==R.id.recette) {
            Toast.makeText(this, "Switching to Recette Activity!", Toast.LENGTH_LONG).show();
            Intent i = new Intent(this, RecetteActivity.class);
            startActivity(i);
        }
    }

    private class myAdapter extends BaseAdapter{

        LayoutInflater inflater;
        public myAdapter(){
            inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return 40;
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
        public View getView(int position, View recycleView, ViewGroup parent) {
            View v = recycleView;
            if(v == null){
                v = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
                v.setTag(" ("+position+")");
            }

            TextView tv = (TextView)v.findViewById(android.R.id.text1);
            tv.setText("Item "+position+"  "+(String)v.getTag());

            return v;
        }
    }
}