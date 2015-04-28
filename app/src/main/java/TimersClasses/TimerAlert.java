package TimersClasses;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import lesdevoreurs.bon_manger.R;

/**
 * Created by BenZen on 26/04/15.
 *
 * Class file for the alerts given when a timer ends.
 */
/**
public class TimerAlert extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timer_alert);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // Title and dialog
        alertDialogBuilder.setTitle("TimersClasses");
        alertDialogBuilder
                .setMessage("A timer has just finished!")
                .setCancelable(false)
                .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        stopService(getIntent());
                        dialog.cancel();
                        finish();
                    }
                });

        //display the dialog box
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
}}
*/