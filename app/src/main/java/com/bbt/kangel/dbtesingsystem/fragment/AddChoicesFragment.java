package com.bbt.kangel.dbtesingsystem.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatRadioButton;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.bbt.kangel.dbtesingsystem.R;

/**
 * Created by Kangel on 2016/2/22.
 */
public class AddChoicesFragment extends Fragment {
    private EditText editA, editB, editC, editD;
    private AppCompatRadioButton radioA, radioB, radioC, radioD;
    private RadioGroup radioGroup;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_choices, container, false);
        editA = (EditText) v.findViewById(R.id.edit_choice_a);
        editB = (EditText) v.findViewById(R.id.edit_choice_b);
        editC = (EditText) v.findViewById(R.id.edit_choice_c);
        editD = (EditText) v.findViewById(R.id.edit_choice_d);
        radioA = (AppCompatRadioButton) v.findViewById(R.id.radio_a);
        radioB = (AppCompatRadioButton) v.findViewById(R.id.radio_b);
        radioC = (AppCompatRadioButton) v.findViewById(R.id.radio_c);
        radioD = (AppCompatRadioButton) v.findViewById(R.id.radio_d);
        radioGroup = (RadioGroup) v.findViewById(R.id.radio_group);
        if (getActivity() instanceof TextWatcher) {
            editA.addTextChangedListener((TextWatcher) getActivity());
            editB.addTextChangedListener((TextWatcher) getActivity());
            editC.addTextChangedListener((TextWatcher) getActivity());
            editD.addTextChangedListener((TextWatcher) getActivity());
        }
        return v;

    }

    public String getEditA() {
        if (editA != null) {
            return editA.getText().toString();
        }
        return "";
    }

    public String getEditB() {
        if (editB != null) {
            return editB.getText().toString();
        }
        return "";
    }

    public String getEditC() {
        if (editC != null) {
            return editC.getText().toString();
        }
        return "";
    }

    public String getEditD() {
        if (editD != null) {
            return editD.getText().toString();
        }
        return "";
    }

    public boolean isAllFilled() {
        return !(getEditA().isEmpty() || getEditB().isEmpty() || getEditC().isEmpty() || getEditD().isEmpty());
    }

    public String getRadioChecked() {
        if (radioGroup != null) {
            switch (radioGroup.getCheckedRadioButtonId()) {
                case R.id.radio_a:
                    return "A";
                case R.id.radio_b:
                    return "B";
                case R.id.radio_c:
                    return "C";
                case R.id.radio_d:
                    return "D";
                default:
                    return "#";
            }
        }
        return "#";
    }
}
