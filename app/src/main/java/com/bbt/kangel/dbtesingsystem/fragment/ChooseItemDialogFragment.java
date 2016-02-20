package com.bbt.kangel.dbtesingsystem.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.bbt.kangel.dbtesingsystem.util.DialogActivity;

/**
 * Created by Kangel on 2016/2/20.
 */
public class ChooseItemDialogFragment extends DialogFragment {
    public static ChooseItemDialogFragment newInstance(int ArrayResId) {
        ChooseItemDialogFragment f = new ChooseItemDialogFragment();
        Bundle args = new Bundle();
        args.putInt("data", ArrayResId);
        f.setArguments(args);
        return f;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        try {
            int resId = args.getInt("data");
            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setItems(resId, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        Bundle bundle = new Bundle();
                        bundle.putInt("which", which);
                        ((DialogActivity) getActivity()).onDialogItemSelect(getTag(),bundle);
                    } catch (Exception e) {
                        throw new ClassCastException(getActivity().toString() + "must implement DialogActivity");
                    }

                }
            });
            return builder.create();
        } catch (Exception e) {
            throw new NullPointerException(this.toString() + "no 'data' in args bundle");
        }
    }
}
