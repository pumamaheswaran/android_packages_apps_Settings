package com.android.settings.gestures;

import android.os.Bundle;
import android.app.Fragment;
import com.android.settings.R;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MultiTouchFragment extends Fragment {

    public MultiTouchFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_multi_touch, container, false);
    }
}

