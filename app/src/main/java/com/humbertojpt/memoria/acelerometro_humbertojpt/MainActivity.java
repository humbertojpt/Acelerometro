package com.humbertojpt.memoria.acelerometro_humbertojpt;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements SensorEventListener, AdapterView.OnItemSelectedListener{

    private SensorManager mSensorManager;
    private Sensor mAcelSensor;
    private TextView mEditTextX;
    private TextView mEditTextY;
    private TextView mEditTextZ;
    private Spinner spinner ,spinner2;
    private int state , state2;
    private static final String[]levels = {"Normal", "Game", "Faster"};
    private static final String[]type = {"Acelerometro", "A. Lineal"};
    private static final int MIN_SHAKE_ACCELERATION = 10;

    // Arrays to store gravity and linear acceleration values
    private float[] mGravity = { 0.0f, 0.0f, 0.0f };
    private float[] mLinearAcceleration = { 0.0f, 0.0f, 0.0f };

    // Indexes for x, y, and z values
    private static final int X = 0;
    private static final int Y = 1;
    private static final int Z = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mEditTextX = (TextView) findViewById(R.id.textView2);
        mEditTextY = (TextView) findViewById(R.id.textView4);
        mEditTextZ = (TextView) findViewById(R.id.textView6);
        mEditTextX.setText("");
        mEditTextY.setText("");
        mEditTextZ.setText("");

        spinner = (Spinner)findViewById(R.id.spinner1);
        ArrayAdapter<String>adapter = new ArrayAdapter<String>(MainActivity.this,
                android.R.layout.simple_spinner_item,levels);

        spinner2 = (Spinner)findViewById(R.id.spinner2);
        ArrayAdapter<String>adapter2 = new ArrayAdapter<String>(MainActivity.this,
                android.R.layout.simple_spinner_item,type);

        //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        spinner2.setAdapter(adapter2);
        spinner2.setOnItemSelectedListener(this);
    }

    @Override
    public void onSaveInstanceState(Bundle saveInstanceState){

        super.onSaveInstanceState(saveInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        stopCapturing();
        mEditTextX.setText("");
        mEditTextY.setText("");
        mEditTextZ.setText("");
    }

    @Override
    public void onStop() {
        super.onStop();
        stopCapturing();
        mEditTextX.setText("");
        mEditTextY.setText("");
        mEditTextZ.setText("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {


        Spinner spinner = (Spinner) parent;
        if(spinner.getId() == R.id.spinner1)
        {
            switch (position) {
                case 0:
                    state = 0;
                    break;
                case 1:
                    state = 1;
                    break;
                case 2:
                    state = 2;
                    break;

            }
        }
        else if(spinner.getId() == R.id.spinner2)
        {
            switch (position) {
                case 0:
                    state2 = 0;
                    break;
                case 1:
                    state2 = 1;
                    break;
            }
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void startCapture() {
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        switch (state2) {
           case 0:
               mAcelSensor = mSensorManager
                       .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
               break;
           case 1:
               mAcelSensor = mSensorManager
                       .getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
                break;
        }


        switch (state) {
            case 0:
                mSensorManager.registerListener(this, mAcelSensor,
                        SensorManager.SENSOR_DELAY_NORMAL);
                break;
            case 1:
                mSensorManager.registerListener(this, mAcelSensor,
                        SensorManager.SENSOR_DELAY_GAME);
                break;
            case 2:
                mSensorManager.registerListener(this, mAcelSensor,
                        SensorManager.SENSOR_DELAY_FASTEST);
                break;

        }

    }

    public void stopCapturing() {
        if (mSensorManager != null) {
            mSensorManager.unregisterListener(this, mAcelSensor);
        } else {
           // Toast.makeText(getApplicationContext(),
             //       "mSensorManager null", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        stopCapturing();
        mEditTextX.setText("");
        mEditTextY.setText("");
        mEditTextZ.setText("");
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        mEditTextX.setText(event.values[0] + "");
        mEditTextY.setText(event.values[1] + "");
        mEditTextZ.setText(event.values[2] + "");
        setCurrentAcceleration(event);
        float maxLinearAcceleration = getMaxCurrentLinearAcceleration();

        // Check if the acceleration is greater than our minimum threshold
        if (maxLinearAcceleration > MIN_SHAKE_ACCELERATION) {
            Toast.makeText(MainActivity.this, "SE reinicia?", Toast.LENGTH_SHORT).show();
            //onRestart();

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void start(View view) {
        startCapture();
    }

    public void stop(View view) {
        stopCapturing();
    }

    private void setCurrentAcceleration(SensorEvent event) {


        final float alpha = 0.8f;

        // Gravity components of x, y, and z acceleration
        mGravity[X] = alpha * mGravity[X] + (1 - alpha) * event.values[X];
        mGravity[Y] = alpha * mGravity[Y] + (1 - alpha) * event.values[Y];
        mGravity[Z] = alpha * mGravity[Z] + (1 - alpha) * event.values[Z];

        // Linear acceleration along the x, y, and z axes (gravity effects removed)
        mLinearAcceleration[X] = event.values[X] - mGravity[X];
        mLinearAcceleration[Y] = event.values[Y] - mGravity[Y];
        mLinearAcceleration[Z] = event.values[Z] - mGravity[Z];

        /*
         *  END SECTION from Android developer site
         */
    }

    private float getMaxCurrentLinearAcceleration() {
        // Start by setting the value to the x value
        float maxLinearAcceleration = mLinearAcceleration[X];

        // Check if the y value is greater
        if (mLinearAcceleration[Y] > maxLinearAcceleration) {
            maxLinearAcceleration = mLinearAcceleration[Y];
        }

        // Check if the z value is greater
        if (mLinearAcceleration[Z] > maxLinearAcceleration) {
            maxLinearAcceleration = mLinearAcceleration[Z];
        }

        // Return the greatest value
        return maxLinearAcceleration;
    }

}
