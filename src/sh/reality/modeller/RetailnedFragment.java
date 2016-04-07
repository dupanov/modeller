package sh.reality.modeller;

import android.app.Fragment;
import android.os.Bundle;

import java.util.ArrayList;

/**
 * Created by Dupanov on 04.04.2016.
 */
public class RetailnedFragment extends Fragment {

    // data object we want to retain
    private ArrayList<String> data;

    // this method is only called once for this fragment
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // retain this fragment
        setRetainInstance(true);
    }

    public void setData(ArrayList<String> data) {
        this.data = data;
    }

    ArrayList<String> getData() {
        return data;
    }

}
