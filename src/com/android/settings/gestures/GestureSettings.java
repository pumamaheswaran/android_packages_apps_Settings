package com.android.settings.gestures;

import android.os.Bundle;
import android.app.Fragment;
import com.android.settings.R;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.view.MenuItem;
import android.view.Menu;
import android.view.MenuInflater;
import android.content.res.TypedArray;
import com.android.settings.SettingsActivity;
import android.widget.TextView;
import com.android.settings.SettingsPreferenceFragment;

public class GestureSettings extends SettingsPreferenceFragment {

    private static final int MENU_ID_ADD_GESTURE = 1;
    private GesturesEnabler mGesturesEnabler;
    private TextView mEmptyView;

    public GestureSettings() {

    }    

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        
        addOptionsMenuItems(menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    void addOptionsMenuItems(Menu menu) {

        TypedArray ta = getActivity().getTheme().obtainStyledAttributes(
                new int[] {R.attr.ic_menu_add, R.attr.ic_wps});
        menu.add(Menu.NONE, MENU_ID_ADD_GESTURE, 0, R.string.add_gesture)
                .setIcon(ta.getDrawable(0))
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);        
        ta.recycle();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_ID_ADD_GESTURE:
                startFragment(this, MultiTouchFragment.class.getCanonicalName(),
                            R.string.gesture_settings_title, -1, null);
                return true;            
        }
        return super.onOptionsItemSelected(item);
    }

    private void showToast() {
        Toast.makeText(getActivity().getApplicationContext(), "Clicked", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStart() {
        super.onStart();

        // On/off switch is hidden for Setup Wizard (returns null)
        mGesturesEnabler = createGesturesEnabler();
    }

    GesturesEnabler createGesturesEnabler() {
        final SettingsActivity activity = (SettingsActivity) getActivity();
        return new GesturesEnabler(activity, activity.getSwitchBar());
    }

    protected TextView initEmptyView() {
        TextView emptyView = (TextView) getActivity().findViewById(android.R.id.empty);
        getListView().setEmptyView(emptyView);
        emptyView.setText(R.string.gestures_not_enabled_message);
        return emptyView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        mEmptyView = initEmptyView();
    }
}

