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
import android.widget.TextView;

import static android.content.Context.SENSOR_SERVICE;

public class InProcessFragment extends Fragment implements SensorListener {

    public InProcessFragment() {
    }

    private TextView tvThreshold, tvCurrent, tvPeak;
    SensorManager sensorMgr;

    Context context;

    SharedPreferences sharedPref;
    int defaultValue;
    float highScore;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_inprocess_test, container, false);
        sensorMgr = (SensorManager) getActivity().getSystemService(SENSOR_SERVICE);
        sensorMgr.registerListener(this, SensorManager.SENSOR_ACCELEROMETER, SensorManager.SENSOR_DELAY_GAME);
        tvThreshold = rootView.findViewById(R.id.tvThreshold);
        tvCurrent   = rootView.findViewById(R.id.tvCurrent);
        tvPeak      = rootView.findViewById(R.id.tvPeak);

        context=getActivity();
        sharedPref=context.getSharedPreferences("Threshold",Context.MODE_PRIVATE);
        //defaultValue = getResources().getInteger(R.integer.saved_high_score_default_key);
        highScore = sharedPref.getFloat("Thres_value",24.00f);
        return rootView;
    }

    float peakValue = 0.0f;
    int counter=0;
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
            if(Math.abs(sqrt-9)>highScore&&counter==0){
                //Intent intent=new Intent(getContext(),CountDownFragment.class);
                //startActivity(intent);
                Fragment newFragment=new CountDownFragment();
                FragmentTransaction fragmentTransaction=getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.content_frame,newFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                counter++;
            }
        }
    }

    @Override
    public void onAccuracyChanged(int i, int i1) {
        //tvCurrent.setText(i+" "+i1);
    }
}