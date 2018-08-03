package com.bowenchin.android.noter;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.app.DatePickerDialog.OnDateSetListener;


import java.util.Calendar;

/**
 * Created by bowenchin on 28/12/15.
 *
 * A lightweight wrapper for a DatePickerDIalog that wraps the dialog in a fragment.
 */
public class DatePickerDialogFragment extends DialogFragment {
    static final String YEAR = "year";
    static final String MONTH = "month";
    static final String DAY = "day";

    public static DatePickerDialogFragment newInstance(Calendar date){
        DatePickerDialogFragment fragment = new DatePickerDialogFragment();
        Bundle args = new Bundle();
        args.putInt(YEAR, date.get(Calendar.YEAR));
        args.putInt(MONTH, date.get(Calendar.MONTH));
        args.putInt(DAY, date.get(Calendar.DAY_OF_MONTH));
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        OnDateSetListener callback = (OnDateSetListener)
                getFragmentManager().findFragmentByTag(TaskEditFragment.DEFAULT_FRAGMENT_TAG);
        Bundle args = getArguments();
        return new DatePickerDialog(getActivity(), callback,args.getInt(YEAR),args.getInt(MONTH),args.getInt(DAY));
    }


}
