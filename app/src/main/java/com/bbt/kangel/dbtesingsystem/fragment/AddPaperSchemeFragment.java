package com.bbt.kangel.dbtesingsystem.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.bbt.kangel.dbtesingsystem.R;

/**
 * Created by Kangel on 2016/2/22.
 */
public class AddPaperSchemeFragment extends Fragment implements TextWatcher {
    private EditText choiceCount, gapCount, essayCount;
    private EditText choiceScore, gapScore, essayScore;
    private EditText totalScore;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_paper_scheme, container, false);

        choiceCount = (EditText) v.findViewById(R.id.edit_choice_count);
        gapCount = (EditText) v.findViewById(R.id.edit_gap_count);
        essayCount = (EditText) v.findViewById(R.id.edit_essay_count);

        choiceScore = (EditText) v.findViewById(R.id.edit_choice_score);
        gapScore = (EditText) v.findViewById(R.id.edit_gap_score);
        essayScore = (EditText) v.findViewById(R.id.edit_essay_score);

        totalScore = (EditText) v.findViewById(R.id.edit_total_score);

        choiceCount.addTextChangedListener(this);
        gapCount.addTextChangedListener(this);
        essayCount.addTextChangedListener(this);
        choiceScore.addTextChangedListener(this);
        gapScore.addTextChangedListener(this);
        essayScore.addTextChangedListener(this);

        if (getActivity() instanceof TextWatcher) {
            totalScore.addTextChangedListener(((TextWatcher) getActivity()));
        }
        return v;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        totalScore.setText(String.valueOf(getTotal()));
    }

    public int getChoiceCount() {
        if (choiceCount != null && !choiceCount.getText().toString().isEmpty()) {
            return Integer.parseInt(choiceCount.getText().toString());
        } else return 0;
    }

    public int getGapCount() {
        if (gapCount != null && !gapCount.getText().toString().isEmpty()) {
            return Integer.parseInt(gapCount.getText().toString());
        } else return 0;
    }

    public int getEssayCount() {
        if (essayCount != null && !essayCount.getText().toString().isEmpty()) {
            return Integer.parseInt(essayCount.getText().toString());
        } else return 0;
    }

    public int getChoiceScore() {
        if (choiceScore != null && !choiceScore.getText().toString().isEmpty()) {
            return Integer.parseInt(choiceScore.getText().toString());
        } else return 0;
    }

    public int getGapScore() {
        if (gapScore != null && !gapScore.getText().toString().isEmpty()) {
            return Integer.parseInt(gapScore.getText().toString());
        } else return 0;
    }

    public int getEssayScore() {
        if (essayScore != null && !essayScore.getText().toString().isEmpty()) {
            return Integer.parseInt(essayScore.getText().toString());
        } else return 0;
    }

    public int getTotal() {
        return getChoiceCount() * getChoiceScore() + getGapCount() * getGapScore() + getEssayCount() * getEssayScore();
    }
}
