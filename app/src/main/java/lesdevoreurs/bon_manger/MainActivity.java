package lesdevoreurs.bon_manger;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;  //deprecated  ლ(ಠ益ಠ)ლ
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import Adapter.NavDrawerListAdapter;
import Fragments.CurrentRecipe_Fragment;
import Fragments.Home_Fragment;
import Fragments.Liste_Fragment;
import Fragments.Livre_Fragment;
import Fragments.Menu_Fragment;
import Fragments.Timer_Fragment;
import SlidingMenu.NavDrawerItem;

/**
 * Application bon_manger,
 * You can search for recipe and add them to your cookbook, save one in current recipe, add timers,
 * save a grocery list and planning a menu for your family. Our team also choose you a meal for
 * each month.
 * @serial 893281910023182AB3388EM
 * @author Nicolas, Virgile, Benoit, François
 * @date 30-04-2015
 * @version 4.8.3.0.542.38902.12
 */
public class MainActivity extends Activity {

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle; //deprecated  ლ(ಠ益ಠ)ლ

    private boolean first_fragment;

    private static NavDrawerItem timersDrawer;

    public static NavDrawerItem getTimersDrawer(){return timersDrawer;}

    // nav drawer title
    private CharSequence mDrawerTitle;

    // used to store app title
    private CharSequence mTitle;

    // slide menu items
    private String[] navMenuTitles;
    private TypedArray navMenuIcons;

    private ArrayList<NavDrawerItem> navDrawerItems;
    private NavDrawerListAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTitle = mDrawerTitle = getTitle();

        first_fragment = true;

        // load slide menu items
        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

        // nav drawer icons from resources
        navMenuIcons = getResources()
                .obtainTypedArray(R.array.nav_drawer_icons);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

        navDrawerItems = new ArrayList<NavDrawerItem>();

        // adding nav drawer items to array
        // Home
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
        // Recherche
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
        // Recette en cours
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));
        // Timers ----- le counter peut permettre d'afficher le nomber de timer qui sont actifs mettons
        timersDrawer=new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1), true, Integer.toString(Timer_Fragment.getNumberOfTimer()));
        //navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1), true, Integer.toString(Timer_Fragment.getNumberOfTimer())));
        navDrawerItems.add(timersDrawer);
        // Liste d'epicerie
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons.getResourceId(4, -1)));
        // Menu
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[5], navMenuIcons.getResourceId(5, -1)));
        //Livre de recettes
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[6], navMenuIcons.getResourceId(6, -1)));


        // Recycle the typed array
        navMenuIcons.recycle();

        // setting the nav drawer list adapter
        adapter = new NavDrawerListAdapter(getApplicationContext(),
                navDrawerItems);
        mDrawerList.setAdapter(adapter);

        // enabling action bar app icon and behaving it as toggle button
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.ic_drawer, //nav menu toggle icon
                R.string.app_name, // nav drawer open - description for accessibility
                R.string.app_name // nav drawer close - description for accessibility
        ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                // calling onPrepareOptionsMenu() to show action bar icons
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                // calling onPrepareOptionsMenu() to hide action bar icons
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            // on first time display view for first nav item
            displayView(0);
        }

        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // toggle nav drawer on selecting action bar app icon/title
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action bar actions click
        switch (item.getItemId()) {
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // if nav drawer is opened, hide the action items
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        timersDrawer.setCount(Integer.toString(Timer_Fragment.getNumberOfTimer()));
        adapter.notifyDataSetChanged();
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }



    /**
     * Diplaying fragment view for selected nav drawer list item
     *
     * Drawer menu made using code and tutorial from http://www.androidhive.info/2013/11/android-sliding-menu-using-navigation-drawer/
     *
     */
    private void displayView(int position) {
        // update the main content by replacing fragments
        Fragment fragment = null;
        switch (position) {
            case 0: //home
                fragment = new Home_Fragment();
                break;
            case 1: //Recherche
                fragment = new Research_Fragment1();
                break;
            case 2: //Recette en cours
                fragment = new CurrentRecipe_Fragment();
                break;
            case 3: //Timers
                fragment = new Timer_Fragment();
                break;
            case 4: //Liste d'epicerie
                fragment = Liste_Fragment.getInstance();
                break;
            case 5:// Menu
                fragment = new Menu_Fragment();
                break;
            case 6: //Livre
                fragment = new Livre_Fragment();
                break;

            default:
                break;
        }

        if (fragment != null) {
            if (!first_fragment) {  //addToBackStack
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.frame_container, fragment).addToBackStack("back").commit();

                // update selected item and title, then close the drawer
                mDrawerList.setItemChecked(position, true);
                mDrawerList.setSelection(position);
                setTitle(navMenuTitles[position]);
                mDrawerLayout.closeDrawer(mDrawerList);

            } else { //do not addToBackStack -> was causing blank page
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.frame_container, fragment).commit();

                // update selected item and title, then close the drawer
                mDrawerList.setItemChecked(position, true);
                mDrawerList.setSelection(position);
                setTitle(navMenuTitles[position]);
                mDrawerLayout.closeDrawer(mDrawerList);
                first_fragment = false;
            }

        } else {
            // error in creating fragment
            Log.e("MainActivity", "Error in creating fragment");
        }
    }

    /**
     * Slide menu item click listener
     */
    private class SlideMenuClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            // display view for selected nav drawer item
            displayView(position);
        }
    }
    public void onQtButtonClick(View vi){
        View v = (View) vi.getParent();
        TextView iTextView = (TextView) v.findViewById(R.id.ingTextView);
        String ing = iTextView.getText().toString();
        Liste_Fragment fragment= Liste_Fragment.getInstance();
        fragment.quantity(ing.substring(2));
    }
    public void onMetButtonClick(View vi){
        View v = (View) vi.getParent();
        TextView iTextView = (TextView) v.findViewById(R.id.ingTextView);
        String ing = iTextView.getText().toString();
        Liste_Fragment fragment= Liste_Fragment.getInstance();
        fragment.metrique(ing.substring(2));
    }
    public void onCheck(View vi){
        View v = (View) vi.getParent();
        TextView iTextView = (TextView) v.findViewById(R.id.ingTextView);
        String ing = iTextView.getText().toString();
        Liste_Fragment fragment= Liste_Fragment.getInstance();
        fragment.check(ing.substring(2));
    }
    public void onAdd(View vi){
        Liste_Fragment fragment= Liste_Fragment.getInstance();
        fragment.add();
    }
    public void onDel(View vi){
        Liste_Fragment fragment= Liste_Fragment.getInstance();
        fragment.del();
    }
};