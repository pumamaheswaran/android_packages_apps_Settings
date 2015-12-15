package com.android.settings.gestures;

import android.os.Bundle;
import android.app.Fragment;
import com.android.settings.R;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.util.Log;
import android.widget.RelativeLayout;
import android.app.DialogFragment;
import android.view.GestureRecorder;

public class MultiTouchFragment extends Fragment implements SaveGesturesCallback,SaveGesturesDialogFragment.SaveGesturesDialogListener {

    private static final String TAG = "MultiTouchFragment";
    private MultiTouchView mMultiTouchView;

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
        View mView = inflater.inflate(R.layout.activity_multi_touch, container, false);

        for(int i=0;i<((RelativeLayout)mView).getChildCount();i++){
            
            View child=((RelativeLayout)mView).getChildAt(i);
            if(child instanceof MultiTouchView) {
                mMultiTouchView = (MultiTouchView)child;
                //Log.d(TAG,"Setting reference to this object");
                ((MultiTouchView)child).setBaseFragment(this);    
            }
        }

        return mView;
    }

    public void fireSaveGestureEvent() {
        /*Log.d(TAG,"Inside fireSaveGestureEvent()");
        Toast.makeText(getActivity(), "Hello", Toast.LENGTH_SHORT).show();*/
        SaveGesturesDialogFragment dialog = new SaveGesturesDialogFragment();
        dialog.setParentFragment(this);
        dialog.show(getFragmentManager(), "SaveGesturesDialogFragment");
    }

    public void onDialogPositiveClick(DialogFragment fragment, String gestureName, String actionType) {
        //Toast.makeText(getActivity(),"YES",Toast.LENGTH_SHORT).show();
        mMultiTouchView.saveEvent(gestureName, actionType);
    }
    public void onDialogNegativeClick(DialogFragment fragment) {
        mMultiTouchView.eventCancelSave();        
    }

}

