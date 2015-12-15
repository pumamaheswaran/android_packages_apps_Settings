package com.android.settings.gestures;

import android.app.DialogFragment;
import android.app.AlertDialog;
import android.os.Bundle;
import android.content.DialogInterface;
import android.app.Dialog;
import com.android.settings.R;
import android.view.LayoutInflater;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import java.lang.CharSequence;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

public class SaveGesturesDialogFragment extends DialogFragment {

  public interface SaveGesturesDialogListener {
    public void onDialogPositiveClick(DialogFragment fragment, String gestureName, String actionType);
    public void onDialogNegativeClick(DialogFragment fragment);
  }

  SaveGesturesDialogListener mListener;
  SaveGesturesDialogFragment mThis;
  private Spinner actionSpinner;
  private EditText gestureName;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View tempView = inflater.inflate(R.layout.save_gestures_dialog,null);
        gestureName = (EditText) tempView.findViewById(R.id.gesturename);
        actionSpinner = (Spinner) tempView.findViewById(R.id.action_spinner);
        builder.setView(tempView)        
               .setPositiveButton("OKAY", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {

                       mListener.onDialogPositiveClick(mThis,gestureName.getText().toString(),
                                 actionSpinner.getSelectedItem().toString());
                   }
               })
               .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       mListener.onDialogNegativeClick(mThis);
                   }
               })
               .setTitle("Save?");
        // Create the AlertDialog object and return it

        Spinner spinner = (Spinner) tempView.findViewById(R.id.action_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
        R.array.actions_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        return builder.create();
    }

    public void setParentFragment(SaveGesturesDialogListener listener) {
      mListener = listener;
      mThis = this;
    }
    
}