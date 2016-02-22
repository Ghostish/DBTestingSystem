package com.bbt.kangel.dbtesingsystem.activity;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.bbt.kangel.dbtesingsystem.R;
import com.bbt.kangel.dbtesingsystem.util.DataBaseHelper;
import com.bbt.kangel.dbtesingsystem.util.GlobalKeeper;

/**
 * Created by Kangel on 2016/2/21.
 */
public class TeacherAddNewTextQuestionActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher {
    private EditText contentEdit, answerEdit;
    private LinearLayout addContentLayout, addAnswerLayout;
    private int atStep = 1;
    private int viewType;
    private AppCompatButton actionButtonRight;
    private AppCompatButton actionButtonLeft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_add_content);
        Bundle args = getIntent().getExtras();
        try {
            viewType = args.getInt("TYPE");
        } catch (Exception e) {
            throw new NullPointerException(this.toString() + "must call this activity with type set in bundle");
        }
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
                    moveToFirstStep();
                }
                break;
            }
            case R.id.action_button_right: {
                if (atStep == 1) {
                    moveToSecondStep();
                } else {
                    commitNewQuestionToDB();
                    setResult(RESULT_OK);
                    finish();
                }
                break;
            }
            default: {
                finish();
            }
        }
    }

    private void commitNewQuestionToDB() {
        // TODO: 2016/2/21 use thread to do the following task
        DataBaseHelper helper = new DataBaseHelper(TeacherAddNewTextQuestionActivity.this, GlobalKeeper.DB_NAME, 1);
        SQLiteDatabase db = helper.getWritableDatabase();
        String tableName = null;
        if (viewType == GlobalKeeper.TYPE_GAP) {
            tableName = "gapQuestion";
        }
        if (viewType == GlobalKeeper.TYPE_ESSAY) {
            tableName = "essayQuestion";
        }
        ContentValues value = new ContentValues();
        value.put("CONTENT", contentEdit.getText().toString());
        value.put("ANSWER", answerEdit.getText().toString());
        db.insert(tableName, null, value);
        db.close();
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

    @Override
    public void onBackPressed() {
        if (atStep == 2) {
            moveToFirstStep();
            return;
        }
        super.onBackPressed();
    }

    private void moveToFirstStep() {
        addAnswerLayout.setVisibility(View.INVISIBLE);
        addContentLayout.setVisibility(View.VISIBLE);
        atStep = 1;
        actionButtonRight.setText(R.string.next_step);
        actionButtonLeft.setEnabled(false);
        actionButtonRight.setEnabled(true);
    }

    private void moveToSecondStep() {
        addAnswerLayout.setVisibility(View.VISIBLE);
        addContentLayout.setVisibility(View.INVISIBLE);
        atStep = 2;
        actionButtonLeft.setEnabled(true);
        actionButtonRight.setText(R.string.finish);
        if (answerEdit.getText().toString().isEmpty()) {
            actionButtonRight.setEnabled(false);
        } else {
            actionButtonRight.setEnabled(true);
        }
    }
}
