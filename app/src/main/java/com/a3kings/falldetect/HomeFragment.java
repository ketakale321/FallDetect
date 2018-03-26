package com.a3kings.falldetect;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class HomeFragment extends Fragment implements View.OnClickListener {

    public HomeFragment() {

    }

    View rootView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        doStuffs();
        return rootView;
    }

    ImageView ivStart;
    private void doStuffs() {
        MainActivity.changeTitle("Home");
        ivStart = (ImageView) rootView.findViewById(R.id.ivStart);
        ivStart.setOnClickListener(this);
    }

    Fragment fragment = null;
    @Override
    public void onClick(View view) {
        fragment = new InProcessFragment();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
    }
}