package sh.reality.modeller;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import sh.reality.modeller.R;

import java.util.ArrayList;

/**
 * Created by Dupanov on 23.03.2016.
 */
public class MyAndroidScanner extends Activity implements Button.OnClickListener {
    private static final String TAG = "MyAdnroidScanner";
    public static ArrayList<String> barcodeArray;

    private TextView result;

    private ArrayAdapter<String> adapter;
    private RetailnedFragment savedData;


   // ImageScanner scanner;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scan_main_activity);
        Button buttonScan = (Button) findViewById(R.id.buttonScan);
        result = (TextView)findViewById(R.id.result);
        buttonScan.setOnClickListener(this);
        //array of Strings to store results
        barcodeArray = new ArrayList<>();
        ListView scannedBarcodesList = (ListView) findViewById(R.id.scannedBarcodesList);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1, barcodeArray);
        scannedBarcodesList.setAdapter(adapter);

/*
        int orientation = getRequestedOrientation();
        int rotation = ((WindowManager) getSystemService(
                Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation();
        switch (rotation) {
            case Surface.ROTATION_0:
                orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                break;
            case Surface.ROTATION_90:
                orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                break;
            case Surface.ROTATION_180:
                orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                break;
            default:
                orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                break;
        }
        */

        savedData = new RetailnedFragment();
        savedData.setData(barcodeArray);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //setRequestedOrientation(orientation);

        restoreMe();

    }

    @Override
    public void onResume(){
        super.onResume();
        restoreMe();
    }

    @Override
    public void onPause(){
        super.onPause();
        restoreMe();
    }

    //restore values from fragment
     private void restoreMe() {
        try{
            barcodeArray = savedData.getData();
        } catch(NullPointerException e) {
            Log.e(TAG, "Cannot restore. Stack trace:\n " + e.getStackTrace() );
        }
    }

    //start scaner activity
    @Override
    public void onClick(View v) {
        Intent i = new Intent(this, ScannerActivity.class);
        startActivityForResult(i, 1);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
       if (requestCode == 1) {
           if (resultCode == Activity.RESULT_OK) {
               String res = data.getStringExtra("result");
               addBarcode(res);
           }
           if (resultCode == Activity.RESULT_CANCELED) {
               result.setText(R.string.error);
           }
       }
    }

    //adds scanned result to result array
    private void addBarcode(String result) {
        if (!barcodeArray.contains(result)) {
            barcodeArray.add(result);
            adapter.notifyDataSetChanged();
        } else {
            Toast.makeText(getApplicationContext(), R.string.alreadyScanned, Toast.LENGTH_SHORT).show();
        }
    }

}
