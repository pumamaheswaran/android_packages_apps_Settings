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
import android.util.Log;
import java.util.ArrayList;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.view.GestureRecorder;
import android.view.Gesture;
import java.util.HashMap;
import java.util.Set;
import android.widget.AdapterView;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import java.util.Collection;
import android.view.Event;
import java.lang.String;


public class GestureSettings extends SettingsPreferenceFragment implements GesturesEnabler.GesturesEnablerListener {

    private static final String TAG = "GestureSettings";
    private static final int MENU_ID_ADD_GESTURE = 1;
    private GesturesEnabler mGesturesEnabler;
    private TextView mEmptyView;
    private GestureRecorder mGestureRecorder;

    public GestureSettings() {
        super();
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
        Log.d(TAG,"Inside onStart()");
        super.onStart();

        // On/off switch is hidden for Setup Wizard (returns null)
        //Log.d(TAG,"Before createGesturesEnabler()");
        mGesturesEnabler = createGesturesEnabler();
        loadGestures();
    }

    GesturesEnabler createGesturesEnabler() {
        //Log.d(TAG,"Inside createGesturesEnabler()");
        final SettingsActivity activity = (SettingsActivity) getActivity();
        GesturesEnabler gestureEnabler = new GesturesEnabler(activity, activity.getSwitchBar()); 
        //Log.d(TAG,"Setting Gestures Enabler Listener");
        gestureEnabler.setGesturesEnablerListener(this);
        //Log.d(TAG,"After setting enabler listener");
        return gestureEnabler;
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
        Log.d(TAG,"Inside onActivityCreated()");
        setHasOptionsMenu(true);
        addPreferencesFromResource(R.xml.gesture_settings);
        mEmptyView = initEmptyView();
        mGestureRecorder = GestureRecorder.getInstance();
        loadGestures();
        registerForContextMenu(getListView());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (mGesturesEnabler != null) {
            mGesturesEnabler.teardownSwitchBar();
        }
    }

    @Override
    public void onResume() {
        
        super.onResume();
        Log.d(TAG,"Inside onResume");
        loadGestures();
        if (mGesturesEnabler != null) {
            mGesturesEnabler.resume(getActivity());
        }        
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mGesturesEnabler != null) {
            mGesturesEnabler.pause();
        }        
    }

    public void fireSwitchChangedEvent(boolean state) {
        
        Log.d(TAG,"Switch Changed Event Fired");
        if(state) {
            loadGestures();
        }
        else {
            //Toast.makeText(getActivity(), "False State", Toast.LENGTH_SHORT);
            //mEmptyView.setText("NO");
            Log.d(TAG,"Reset to null");
            getListView().setAdapter(null);
            getPreferenceScreen().removeAll();
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        if (v.getId()== getListView().getId()) {
    
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
            String[] menuItems = getResources().getStringArray(R.array.actions_array_two);
    
            for(int i = 0; i<menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }   
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int menuItemIndex = item.getItemId();
        String[] menuItems = getResources().getStringArray(R.array.actions_array_two);
        String menuItemName = menuItems[menuItemIndex];
        Gesture et = (Gesture)getListView().getItemAtPosition(info.position);

        int numOfFingers  = Integer.valueOf(String.valueOf(et.getAMoreAccurateDesc().charAt(0)));
        int gestureCode = Event.gestureReverseLookup(et.getAMoreAccurateDesc().substring(9));

        //Log.d(TAG,et.getAMoreAccurateDesc().substring(9));
        //Log.d(TAG,String.valueOf(numOfFingers));
        //Log.d(TAG, String.valueOf(gestureCode));
        
        GestureRecorder.getInstance().deleteGesture(numOfFingers, gestureCode);
        GestureRecorder.getInstance().storeEventToFile();
        GestureRecorder.getInstance().reloadRoot();
        loadGestures();

        //Toast.makeText(getActivity(), ""+ numOfFingers + "" + gestureCode, Toast.LENGTH_SHORT).show();
        //Toast.makeText(getActivity(), et.getAMoreAccurateDesc(), Toast.LENGTH_SHORT).show();
        //Toast.makeText(getActivity(), String.valueOf(et.getAMoreAccurateDesc().substring(9)), Toast.LENGTH_SHORT).show();
        //TextView text = (TextView)findViewById(R.id.footer);
        //text.setText(String.format("Selected %s for item %s", menuItemName, et.getText().toString()));
        //Log.d(TAG,String.format("Selected %s for item %s", menuItemName, et.toString()));
        //Toast.makeText(getActivity(), et.getAMoreAccurateDesc(), Toast.LENGTH_SHORT).show();
        return true;
    }

    private void loadGestures() {
        //Toast.makeText(getActivity(), "True State", Toast.LENGTH_SHORT);
        Log.d(TAG,"Inside Load Gesture");
        ListView listView = getListView();
        HashMap<String,Gesture> gestureMap = mGestureRecorder.retrieveStoredGestureDesriptions();
        
        for (String key : gestureMap.keySet()) {
            Gesture g = gestureMap.get(key);
            g.setAMoreAccurateDesc(key);
        }

        for(String key: gestureMap.keySet()) {
            Log.d(TAG,gestureMap.get(key).getAMoreAccurateDesc());
        }

        Collection<Gesture> keySet = gestureMap.values();
        ArrayList<Gesture> stringList = new ArrayList<Gesture>();
        /*stringList.add("A");
        stringList.add("B");
        stringList.add("C");
        stringList.add("D");*/
        stringList.addAll(keySet);
        
        ArrayAdapter<Gesture> itemsAdapter = new ArrayAdapter<Gesture>(getActivity(), android.R.layout.simple_list_item_1, stringList);
        listView.setAdapter(itemsAdapter);
        itemsAdapter.notifyDataSetChanged();
        //mEmptyView.setText("YES");
        getPreferenceScreen().removeAll();
    }
}

