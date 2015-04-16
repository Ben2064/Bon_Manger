package lesdevoreurs.bon_manger;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;


public class RecetteActivity extends ActionBarActivity {

    ViewPager pager;
    DynamicPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.research_pager);

        pager = (ViewPager)findViewById(R.id.pager);
        adapter = new DynamicPagerAdapter((getSupportFragmentManager()));
        pager.setAdapter(adapter);

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

    private class DynamicPagerAdapter extends FragmentPagerAdapter {

        public DynamicPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            SimplePagerFragment f = new SimplePagerFragment();
            Bundle args = new Bundle();
            args.putInt("id",i);
            f.setArguments(args);
            return f;
        }

        @Override
        public int getCount() {
            return 50;
        }

        public CharSequence getPageTitle(int i){
            return "Page "+(i+1);
        }
    }
}
