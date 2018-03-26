package com.a3kings.falldetect;

import android.database.Cursor;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import static android.content.Context.SENSOR_SERVICE;

public class SettingsFragment extends Fragment {

    public SettingsFragment() {
    }

    ImageView image_shake;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);
        image_shake=rootView.findViewById(R.id.image_shake);

        image_shake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment newFragment=new SetThreshold();
                FragmentTransaction fragmentTransaction=getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.content_frame,newFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
     return rootView;
    }

}