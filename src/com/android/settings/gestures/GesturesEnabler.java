/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings.gestures;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.android.settings.R;
import com.android.settings.WirelessSettings;
import com.android.settings.dashboard.GenericSwitchToggle;
import com.android.settings.search.Index;
import com.android.settings.widget.SwitchBar;

import java.util.concurrent.atomic.AtomicBoolean;

public class GesturesEnabler extends GenericSwitchToggle  {

    private Context mContext;

    public GesturesEnabler(Context context, SwitchBar switchBar) {
        super(context, switchBar);
        mContext = context;
        init();
        setupSwitches();        
    }

    public GesturesEnabler(Context context, Switch switch_) {
        super(context, switch_);
        mContext = context;
        init();
        setupSwitches();
    }

    @Override
    public void resume(Context context) {
        super.resume(context);
    }

    @Override
    public void pause() {
        super.pause();
    }

    @Override
    public void onSwitchChanged(Switch switchView, boolean isChecked) {
        
        //Do nothing if called as a result of a state machine event
        if (mStateMachineEvent) {
            return;
        }
        Toast.makeText(mContext, "Clicked", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        super.onCheckedChanged(buttonView, isChecked);
        Toast.makeText(mContext, "Clicked", Toast.LENGTH_SHORT).show();
    }

    private void setupSwitches() {
        //TODO: Logic for finding the current saved state of the switch and setting the switch to that state.

        if (mSwitchBar != null) {
            mSwitchBar.show();
        }
    }

    private void init() {
        //TODO: Got to figure out, what goes in this function.
    }

    /*private void handleGestureStateChanged(int state) {
        switch (state) {

            case GestureConstants.GESTURE_ON:

            case GestureConstants.GENSTURE_OFF:


        }
    }*/

}
