package fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bbt.kangel.dbtesingsystem.R;
import com.bbt.kangel.dbtesingsystem.util.TestDataBaseUtil;
import com.bbt.kangel.dbtesingsystem.util.mDataBaseHelper;

import adapter.ViewPaperListAdapter;

/**
 * Created by Kangel on 2015/12/14.
 */
public class SelectPaperDialogFragment extends DialogFragment {
    private mDataBaseHelper helper;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE,0);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().setTitle(getActivity().getString(R.string.title_choose_paper));
        View v = inflater.inflate(R.layout.fragment_dialog_choose_paper, container, false);
        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.recycle_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        helper = new mDataBaseHelper(getActivity(), "testing.db", 1);
        ViewPaperListAdapter adapter = new ViewPaperListAdapter(getActivity(), TestDataBaseUtil.getPaperList(helper.getReadableDatabase()));
        recyclerView.setAdapter(adapter);
        return v;
    }

    /*@NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        return builder.create();
    }*/
}
