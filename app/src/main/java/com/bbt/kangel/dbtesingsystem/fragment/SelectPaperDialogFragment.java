package com.bbt.kangel.dbtesingsystem.fragment;

import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bbt.kangel.dbtesingsystem.R;
import com.bbt.kangel.dbtesingsystem.adapter.ViewPaperListAdapter;
import com.bbt.kangel.dbtesingsystem.util.GlobalKeeper;
import com.bbt.kangel.dbtesingsystem.util.TestDataBaseUtil;
import com.bbt.kangel.dbtesingsystem.util.mDataBaseHelper;

/**
 * Created by Kangel on 2015/12/14.
 */
public class SelectPaperDialogFragment extends DialogFragment {
    private mDataBaseHelper helper;
    private Cursor c;
    private SQLiteDatabase db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        Log.d("cycle","onCreate");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //getDialog().setTitle(getActivity().getString(R.string.title_choose_paper));
        View v = inflater.inflate(R.layout.fragment_dialog_choose_paper, container, false);
        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.recycle_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        helper = new mDataBaseHelper(getActivity(), GlobalKeeper.DB_NAME, 1);
        db = helper.getReadableDatabase();
        c = TestDataBaseUtil.getPaperList(db);
        ViewPaperListAdapter adapter = new ViewPaperListAdapter(getActivity(), c);
        recyclerView.setAdapter(adapter);
        Log.d("cycle", "onCreateView");
        return v;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if(db != null &&db.isOpen()){
            db.close();
        }
        if(c != null && !c.isClosed()){
            c.close();
        }
        Log.d("cycle", "onDetach");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("cycle", "onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("cycle", "onDestroy");
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        Log.d("cycle", "onDismiss");
    }
    /*@NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        return builder.create();
    }*/
}
