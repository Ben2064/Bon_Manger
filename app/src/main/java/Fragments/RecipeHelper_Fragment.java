package Fragments;

import android.app.Fragment;

/**
 * fragment that's used to share a recipe with the calendar
 */
public class RecipeHelper_Fragment extends Fragment {

    //private String meal;
    private String date;

   /* public void setMeal(String m){
        this.meal=m;
    }*/
    public void setDate(int d, int m, int y){
        String day = String.format("%02d", d);
        //Months returned by DatePickers start from 0 @January...
        String month = String.format("%02d", m+1);
        String year = Integer.toString(y);
        this.date = day + month + year;
    }
    public String getDate(){
        return this.date;
    }
}
