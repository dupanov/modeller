package sh.reality.modeller;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


import java.io.IOException;

/**
 * Created by admin on 25.03.2016.
 */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = "Reality-Modeller";
    private final Context context;
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private Camera.PreviewCallback previewCallback;
    private Camera.AutoFocusCallback autoFocusCallback;
    private final int laserColor;
    private Paint p;

    private static final int MIN_FRAME_WIDTH = 240;
    private static final int MIN_FRAME_HEIGHT = 240;
    private static final int MAX_FRAME_WIDTH = 1200; // = 5/8 * 1920
    private static final int MAX_FRAME_HEIGHT = 675; // = 5/8 * 1080
    private static final int MIN_PREVIEW_PIXELS = 480 * 320; // normal screen
    private static final float MAX_EXPOSURE_COMPENSATION = 1.5f;
    private static final float MIN_EXPOSURE_COMPENSATION = 0.0f;
    private static final double MAX_ASPECT_DISTORTION = 0.15;
    private static final int MIN_FPS = 10;
    private static final int MAX_FPS = 20;
    private static final int AREA_PER_1000 = 400;

    private Rect framingRect;
    private Rect framingRectInPreview;
    private Point screenResolution;
    private Point cameraResolution;


    public CameraPreview(Context context, Camera camera,
                         Camera.PreviewCallback previewCb,
                         Camera.AutoFocusCallback autoFocusCb) {
        super(context);
        this.context = context;
        Resources resources = getResources();
        mCamera = camera;
        previewCallback = previewCb;
        autoFocusCallback = autoFocusCb;

        laserColor = resources.getColor(R.color.viewfinder_laser);
        p = new Paint();
        p.setAntiAlias(true);
        p.setDither(true);
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeJoin(Paint.Join.MITER);
        p.setStrokeCap(Paint.Cap.SQUARE);
        p.setColor(laserColor);
        p.setStrokeWidth(10);
        p.setAlpha(100);

        /*
         * Set camera to continuous focus if supported, otherwise use
         * software auto-focus. Only works for API level >=9.
         */

        Camera.Parameters parameters = camera.getParameters();
        if (parameters.getSupportedFocusModes().contains(
                Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            autoFocusCallback = null;
            camera.setParameters(parameters);
        }

        Log.i(TAG, "Camera parameters set up: " + parameters);

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);

        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, now tell the camera where to draw the preview.
        try {
            setWillNotDraw(false);
            mCamera.setPreviewDisplay(holder);
        } catch (IOException e) {
            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // Camera preview released in activity
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        /*
         * If your preview can change or rotate, take care of those events here.
         * Make sure to stop the preview before resizing or reformatting it.
         */
        if (mHolder.getSurface() == null) {
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e) {
            // ignore: tried to stop a non-existent preview
        }
       // invalidate();


        try {
            // Hard code camera surface rotation 90 degs to match Activity view in portrait
            mCamera.setDisplayOrientation(90);

            mCamera.setPreviewDisplay(mHolder);
            mCamera.setPreviewCallback(previewCallback);
            mCamera.startPreview();
            mCamera.autoFocus(autoFocusCallback);
        } catch (Exception e) {
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }



    @Override
    protected void onDraw(Canvas canvas) {
       // super.draw(canvas);
       // canvas = mHolder.lockCanvas();

        int centerx = canvas.getClipBounds().centerX();
        int centery = canvas.getClipBounds().centerY();
       // Camera.Size cameraPreviewSize = mCamera.getParameters().getPreviewSize();
        canvas.drawLine(0,centery,canvas.getWidth(), centery,p);
        canvas.drawLine(centerx,0,centerx,canvas.getHeight(),p);
        Log.i(TAG, "Drawing...");
        Log.w(this.getClass().getName(), "On Draw Called");
    }
}
