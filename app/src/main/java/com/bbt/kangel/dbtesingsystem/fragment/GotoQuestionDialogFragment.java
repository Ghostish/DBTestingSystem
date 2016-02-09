package com.bbt.kangel.dbtesingsystem.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bbt.kangel.dbtesingsystem.R;
import com.bbt.kangel.dbtesingsystem.adapter.QuestionNumListAdapter;

/**
 * Created by Kangel on 2016/2/7.
 */
public class GotoQuestionDialogFragment extends DialogFragment {
   static public GotoQuestionDialogFragment newInstance(boolean [] data) {
        GotoQuestionDialogFragment f = new GotoQuestionDialogFragment();
        Bundle args = new Bundle();
        args.putBooleanArray("data", data);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE,0);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        boolean [] data = getArguments()!=null?getArguments().getBooleanArray("data"):null;
        //getDialog().setTitle(getActivity().getString(R.string.title_choose_num));
        View v = inflater.inflate(R.layout.fragment_dialog_choose_question, container, false);
        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.recycle_view);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 5));
        recyclerView.setHasFixedSize(true);
        QuestionNumListAdapter adapter = new QuestionNumListAdapter(getActivity(),data);
        recyclerView.setAdapter(adapter);
        return v;
    }
}
