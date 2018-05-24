package com.eventiotic.hacktheview;

import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraAccessException;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SizeF;

import com.eventiotic.hacktheview.utils.Utils;

public class CameraViewActivity extends AppCompatActivity {
    double horizontalAngle;
    double verticalAngle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_view);

        SizeF size = new SizeF(0,0);
        CameraManager manager =
                (CameraManager)getSystemService(CAMERA_SERVICE);
        try {
            for (String cameraId : manager.getCameraIdList()) {
                CameraCharacteristics chars
                        = manager.getCameraCharacteristics(cameraId);
                int cOrientation = chars.get(CameraCharacteristics.LENS_FACING);
                if (cOrientation == CameraCharacteristics.LENS_FACING_BACK) {
                    size = chars.get(CameraCharacteristics.SENSOR_INFO_PHYSICAL_SIZE);
                    float[] maxFocus = chars.get(CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS);
                    float w = size.getWidth();
                    float h = size.getHeight();
                    horizontalAngle = Utils.radiansToDegrees((double) (2 * Math.atan(w / (maxFocus[0] * 2))));
                    verticalAngle = Utils.radiansToDegrees((double) (2 * Math.atan(h / (maxFocus[0] * 2))));
                }
            }
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        Log.i("", "Hor:"+horizontalAngle);
        Log.i("", "Ver:"+verticalAngle);

    }


}
