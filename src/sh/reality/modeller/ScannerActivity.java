package sh.reality.modeller;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import sh.reality.modeller.CameraPreview;
import net.sourceforge.zbar.*;

/* Import ZBar Class files */

/**
 * Created by admin on 25.03.2016.
 */
public class ScannerActivity extends Activity  {
    private Camera mCamera;
    private CameraPreview mPreview;
    private Handler autoFocusHandler;

    private TextView scanText;
    private Button backButton;
    private Resources resources;

    private ImageScanner scanner;

    //make sounds
    final private ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);


    private boolean barcodeScanned = false;
    private boolean previewing = true;

    static {
        System.loadLibrary("iconv");
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        resources = getResources();
        setContentView(R.layout.scanner);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        autoFocusHandler = new Handler();
        mCamera = getCameraInstance();

        /* Instance barcode scanner */
        scanner = new ImageScanner();
        scanner.setConfig(0, Config.X_DENSITY, 3);
        scanner.setConfig(0, Config.Y_DENSITY, 3);

        mPreview = new CameraPreview(this, mCamera, previewCb, autoFocusCB);
        FrameLayout preview = (FrameLayout)findViewById(R.id.cameraPreview);
        preview.addView(mPreview);

        String scanInstruction = getString(R.string.scanInstruction);
        scanText = (TextView)findViewById(R.id.scanText);
        scanText.setText(scanInstruction);

        if (barcodeScanned) {
            barcodeScanned = false;
            // scanText.setText("Scanning...");
            mCamera.setPreviewCallback(previewCb);
            mCamera.startPreview();
            previewing = true;
            mCamera.autoFocus(autoFocusCB);
        }

            //  backButton = (Button)findViewById(R.id.BackButton);

      //  backButton.setOnClickListener(new View.OnClickListener() {
      //      public void onClick(View v) {
      //          finish();
      //      }
      //  });
    }

    public void onPause() {
        super.onPause();
        releaseCamera();
    }

    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open();
        } catch (Exception e){
        }
        return c;
    }

    private void releaseCamera() {
        if (mCamera != null) {
            previewing = false;
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }
    }

    private Runnable doAutoFocus = new Runnable() {
        public void run() {
            if (previewing)
                mCamera.autoFocus(autoFocusCB);
        }
    };

    Camera.PreviewCallback previewCb = new Camera.PreviewCallback() {
        public void onPreviewFrame(byte[] data, Camera camera) {
            Camera.Parameters parameters = camera.getParameters();
            Camera.Size size = parameters.getPreviewSize();

            Image barcode = new Image(size.width, size.height, "Y800");
            barcode.setData(data);

            int result = scanner.scanImage(barcode);

            if (result != 0) {
                previewing = false;
                mCamera.setPreviewCallback(null);
                mCamera.stopPreview();

                SymbolSet syms = scanner.getResults();
                StringBuilder res = new StringBuilder("");
                for (Symbol sym : syms) {

                    String getBarcode = String.format(getString(R.string.getBarcode), sym.getData());
                    scanText.setText(getBarcode);
                    res.append(sym.getData());
                    barcodeScanned = true;
                }
                //create beep sound
                tg.startTone(ToneGenerator.TONE_PROP_BEEP);
                Intent returnIntent = new Intent();
                String barcodeValue = String.valueOf(res);
                if (barcodeValue.isEmpty()) {
                    setResult(Activity.RESULT_CANCELED, returnIntent);
                } else {
                    returnIntent.putExtra("result", barcodeValue);
                    setResult(Activity.RESULT_OK, returnIntent);
                }

                finish();
            }
        }
    };

    // Mimic continuous auto-focusing
    Camera.AutoFocusCallback autoFocusCB = new Camera.AutoFocusCallback() {
        public void onAutoFocus(boolean success, Camera camera) {
            autoFocusHandler.postDelayed(doAutoFocus, 1000);
        }
    };
}
