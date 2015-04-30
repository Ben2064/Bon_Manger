package Fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.Toast;
import Fragments.LivreRecette_Fragment;

import java.util.Calendar;

/**
 * Created by virgile on 30/04/2015.
 * Used this for reference:
 * http://developer.android.com/guide/topics/ui/controls/pickers.html#DatePicker
 */
public class DatePicker_Fragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);


        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Send recipe to menu
       // Toast.makeText(getActivity(), "Add to menu"+year+"m"+month+"d"+day, Toast.LENGTH_LONG).show();
        RecipeHelper_Fragment target=(RecipeHelper_Fragment)getTargetFragment();
        target.setDate(day,month,year);
    }
}
