package fragment;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.bbt.kangel.dbtesingsystem.R;
import com.bbt.kangel.dbtesingsystem.util.DialogActivity;

/**
 * Created by Kangel on 2015/12/15.
 */
public class ConfirmAlertDialogFragment extends DialogFragment {
    public static ConfirmAlertDialogFragment newInstance(int style, String title, String msg) {
        ConfirmAlertDialogFragment f = new ConfirmAlertDialogFragment();
        Bundle args = new Bundle();
        args.putInt("style", style);
        args.putString("title", title);
        args.putString("msg", msg);
        f.setArguments(args);
        return f;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        int style = args.getInt("style");
        String title = args.getString("title");
        String msg = args.getString("msg");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), style);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setNegativeButton(R.string.cancel_button, null);
        builder.setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ((DialogActivity) getActivity()).doAtPositiveButton(getTag());
            }
        });
        return builder.create();
    }
}
