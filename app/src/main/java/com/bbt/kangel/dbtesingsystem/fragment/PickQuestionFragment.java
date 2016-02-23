package com.bbt.kangel.dbtesingsystem.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bbt.kangel.dbtesingsystem.R;
import com.bbt.kangel.dbtesingsystem.adapter.ViewQuestionListAdapter;
import com.bbt.kangel.dbtesingsystem.util.GlobalKeeper;

/**
 * Created by Kangel on 2016/2/22.
 */
public class PickQuestionFragment extends Fragment {
    private Cursor cursor;
    private int viewType;
    private TextView titleText;
    private String formatString;
    private int totalCount;

    public static PickQuestionFragment newInstance(int viewType, Cursor cursor,int totalCount) {
        PickQuestionFragment pickQuestionFragment = new PickQuestionFragment();
        pickQuestionFragment.viewType = viewType;
        pickQuestionFragment.cursor = cursor;
        pickQuestionFragment.totalCount = totalCount;
        return pickQuestionFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_pick_question, container, false);

        String recyclerViewTag = null;
        switch (viewType) {
            case GlobalKeeper.TYPE_CHOICE:
                formatString = getActivity().getString(R.string.title_pick_choices);
                recyclerViewTag = "choices";
                break;
            case GlobalKeeper.TYPE_GAP:
                formatString = getActivity().getString(R.string.title_pick_gaps);
                recyclerViewTag = "gaps";
                break;
            case GlobalKeeper.TYPE_ESSAY:
                formatString = getActivity().getString(R.string.title_pick_essays);
                recyclerViewTag = "essays";
        }
        RecyclerView rv = (RecyclerView) v.findViewById(R.id.recycle_view);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        ViewQuestionListAdapter adapter = new ViewQuestionListAdapter(getActivity(), cursor, true, false, recyclerViewTag);
        rv.setAdapter(adapter);

        titleText = (TextView) v.findViewById(R.id.title_pick_question);
        setTitleTextNum(0, totalCount);
        return v;
    }

    public void setTitleTextNum(int current, int total) {
        if (titleText != null && formatString != null) {
            titleText.setText(String.format(formatString, current, total));
        }
    }
}
