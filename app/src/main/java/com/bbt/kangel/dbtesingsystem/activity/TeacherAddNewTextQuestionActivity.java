package com.bbt.kangel.dbtesingsystem.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.bbt.kangel.dbtesingsystem.R;

/**
 * Created by Kangel on 2016/2/21.
 */
public class TeacherAddNewTextQuestionActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher {
    private EditText contentEdit, answerEdit;
    private LinearLayout addContentLayout, addAnswerLayout;
    private int atStep = 1;
    private AppCompatButton actionButtonRight;
    private AppCompatButton actionButtonLeft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_add_content);

        addContentLayout = (LinearLayout) findViewById(R.id.add_content_layout);
        addAnswerLayout = (LinearLayout) findViewById(R.id.add_answer_layout);
        contentEdit = (EditText) findViewById(R.id.question_content);
        answerEdit = (EditText) findViewById(R.id.question_answer);
        actionButtonLeft = (AppCompatButton) findViewById(R.id.action_button_left);
        actionButtonRight = (AppCompatButton) findViewById(R.id.action_button_right);
        contentEdit.addTextChangedListener(this);
        answerEdit.addTextChangedListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.action_button_left: {
                if (atStep == 2) {
                    addAnswerLayout.setVisibility(View.INVISIBLE);
                    addContentLayout.setVisibility(View.VISIBLE);
                    atStep = 1;
                    actionButtonRight.setText(R.string.next_step);
                    actionButtonLeft.setEnabled(false);
                    actionButtonRight.setEnabled(true);
                }
                break;
            }
            case R.id.action_button_right: {
                if (atStep == 1) {
                    addAnswerLayout.setVisibility(View.VISIBLE);
                    addContentLayout.setVisibility(View.INVISIBLE);
                    atStep = 2;
                    actionButtonLeft.setEnabled(true);
                    actionButtonRight.setText(R.string.finish);
                    actionButtonRight.setEnabled(false);
                }
                break;
            }
            default: {
                finish();
            }
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (!s.toString().isEmpty()) {
            actionButtonRight.setEnabled(true);
        } else {
            actionButtonRight.setEnabled(false);
        }
    }
}
