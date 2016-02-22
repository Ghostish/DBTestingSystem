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
 * Created by Kangel on 2016/2/21.
 */
public class AddQuestionContentFragment extends Fragment {
    private EditText contentEdit;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_question_content, container, false);
        contentEdit = (EditText) v.findViewById(R.id.question_content);
        contentEdit.addTextChangedListener(getActivity() instanceof TextWatcher ? (TextWatcher) getActivity() : null);

        return v;

    }

    public Editable getEditText() {
        return contentEdit.getText();
    }
}
