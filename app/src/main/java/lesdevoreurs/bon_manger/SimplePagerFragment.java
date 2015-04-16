package lesdevoreurs.bon_manger;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Nicolas on 2015-03-19.
 */
public class SimplePagerFragment extends Fragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.research_pager_fragment, container, false);
        Bundle args = getArguments();
        ((TextView)rootView.findViewById(R.id.idText)).setText(" Ceci est la section "+(args.getInt("id")+1));
        return rootView;
    }
}
