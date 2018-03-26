package com.a3kings.falldetect;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import static android.content.Context.SENSOR_SERVICE;

public class SetThreshold extends Fragment implements SensorListener {

    public SetThreshold() {
    }

    private TextView tvThreshold, tvCurrent, tvPeak;
    SensorManager sensorMgr;
    Button set_threshold;
    float peakValue = 0.0f;



    Context context;
    SharedPreferences sharedPref ;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_set_threshold, container, false);
        sensorMgr = (SensorManager) getActivity().getSystemService(SENSOR_SERVICE);
        sensorMgr.registerListener(this, SensorManager.SENSOR_ACCELEROMETER, SensorManager.SENSOR_DELAY_GAME);
        tvThreshold = rootView.findViewById(R.id.st_tvThreshold);
        tvCurrent   = rootView.findViewById(R.id.st_tvCurrent);
        tvPeak      = rootView.findViewById(R.id.st_tvPeak);
        set_threshold=rootView.findViewById(R.id.st_set_threshold);


        context = getActivity();
        sharedPref = context.getSharedPreferences("Threshold", Context.MODE_PRIVATE);



        set_threshold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putFloat("Thres_value",peakValue);
                editor.commit();
                Toast.makeText(getContext(),"Threshold value set succesfullly",Toast.LENGTH_SHORT).show();
            }
        });

        return rootView;
    }


    @Override
    public void onSensorChanged(int i, float[] fArr) {
        if (i == 2) {
            float sqrt = (float) Math.sqrt((double) ((fArr[0] * fArr[0]) + (fArr[1] * fArr[1])));
            sqrt = (float) Math.sqrt((double) ((sqrt * sqrt) + (fArr[2] * fArr[2])))   ;
            tvThreshold.setText( String.format( "%.2f", sqrt )  + "");

            if(peakValue < Math.abs(sqrt-9)){
                peakValue = Math.abs(sqrt-9);
                tvPeak.setText( peakValue + "");
            }
            tvCurrent.setText( String.format( "%.2f", Math.abs(sqrt-9) )  + "");
        }
    }



    @Override
    public void onAccuracyChanged(int i, int i1) {
        //tvCurrent.setText(i+" "+i1);
    }
}