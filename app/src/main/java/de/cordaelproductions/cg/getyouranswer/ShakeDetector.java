package de.cordaelproductions.cg.getyouranswer;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Detection of the device shake
 *
 * based on jasonmcrevnolds.com/?p=388
 * Created by Cordula Gloge on 26/01/2018.
 */

public class ShakeDetector implements SensorEventListener {

    private static final float SHAKE_THRESHOLD_GRAVITY = 2.7F;
    private static final int SHAKE_SLOP_TIME_MS = 500;
    private static final int SHAKE_COUNT_RESET_TIME_MS = 2000;

    private OnShakeListener mListener;
    private long mShakeTimestamp;
    private long mShakeCount;

    public void setOnShakeListener(OnShakeListener listener) {
        this.mListener = listener;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //ignore
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (mListener != null) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            float gX = x / SensorManager.GRAVITY_EARTH;
            float gY = y / SensorManager.GRAVITY_EARTH;
            float gZ = z / SensorManager.GRAVITY_EARTH;

            //gForce will be close to 1 when there is no movement
            float gForce = (float) Math.sqrt(gX * gX + gY * gY + gZ * gZ);
            if (gForce > SHAKE_THRESHOLD_GRAVITY) {
                final long now = System.currentTimeMillis();
                // reset the shake count after 3 seconds of no shakes
                if (mShakeTimestamp + SHAKE_COUNT_RESET_TIME_MS < now) {
                    mShakeCount = 0;
                }

                // ignore shake events too close to each other (500 ms) and mShakeCount > 1.0
                if (mShakeTimestamp + SHAKE_SLOP_TIME_MS < now) {
                    if (mShakeCount > 3.0) {
                        return;
                    }
                }

                mShakeTimestamp = now;
                mShakeCount++;
                // Execute onShake only one time per shake series
                if (mShakeCount == 3.0) {
                    mListener.onShake(mShakeCount);
                }
            }
        }
    }

    public interface OnShakeListener {
        void onShake(long count);
    }
}
