package sh.reality.modeller;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


/**
 * Created by Dupanov on 04.04.2016.
 */
public class ModellerMain extends Activity implements View.OnClickListener {
    @Override
    public void onCreate(Bundle icicle){
        super.onCreate(icicle);
        setContentView(R.layout.main);

        Button close = (Button)findViewById(R.id.close_button);
        close.setOnClickListener(this);
        Resources resources = getResources();

    }

    @Override
    public void onClick(View v){

        switch (v.getId()) {
            case R.id.close_button:
                finish();
                break;
            case R.id.items_in_button:
               {
                   Intent i = new Intent(this, MyAndroidScanner.class);
                   startActivityForResult(i, 1);
               }
            break;
        }
    }
}
