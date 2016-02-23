package com.bbt.kangel.dbtesingsystem.activity;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.bbt.kangel.dbtesingsystem.R;
import com.bbt.kangel.dbtesingsystem.fragment.AddChoicesFragment;
import com.bbt.kangel.dbtesingsystem.fragment.AddQuestionContentFragment;
import com.bbt.kangel.dbtesingsystem.util.DataBaseHelper;
import com.bbt.kangel.dbtesingsystem.util.GlobalKeeper;

/**
 * Created by Kangel on 2016/2/22.
 */
public class TeacherAddChoiceQuestionActivity extends AppCompatActivity implements TextWatcher, View.OnClickListener, FragmentManager.OnBackStackChangedListener {
    private AppCompatButton buttonLeft, buttonRight;
    private AddQuestionContentFragment addQuestionContentFragment;
    private AddChoicesFragment addChoicesFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_teacher_add_wrapper_layout);
        buttonLeft = (AppCompatButton) findViewById(R.id.action_button_left);
        buttonRight = (AppCompatButton) findViewById(R.id.action_button_right);

        getSupportFragmentManager().addOnBackStackChangedListener(this);
        if (savedInstanceState == null) {
            addQuestionContentFragment = new AddQuestionContentFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.add(R.id.wrapper_layout, addQuestionContentFragment, "content");
            ft.commit();
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
        if (addQuestionContentFragment.isVisible()) {
            buttonRight.setEnabled(!s.toString().isEmpty());
        }
        if (addChoicesFragment != null && addChoicesFragment.isVisible()) {
            buttonRight.setEnabled(addChoicesFragment.isAllFilled() && !addChoicesFragment.getRadioChecked().equals("#"));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.action_button_right: {
                if (addQuestionContentFragment.isVisible()) {
                    if (addChoicesFragment == null) {
                        addChoicesFragment = new AddChoicesFragment();
                    }
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction ft = fragmentManager.beginTransaction();
                    ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                    ft.hide(addQuestionContentFragment);
                    ft.add(R.id.wrapper_layout, addChoicesFragment, "choices");
                    ft.addToBackStack(null);
                    ft.commit();
                } else if (addChoicesFragment.isVisible()) {
                    commitToDataBase();
                    setResult(RESULT_OK);
                    finish();
                }
                break;
            }
            case R.id.action_button_left: {
                if (addChoicesFragment.isVisible()) {
                    getSupportFragmentManager().popBackStack();
                }
            }

        }
    }

    @Override
    public void onBackStackChanged() {
        if (addQuestionContentFragment.isVisible()) {
            buttonLeft.setEnabled(false);
            buttonRight.setText(R.string.next_step);
            buttonRight.setEnabled(addQuestionContentFragment.isAdded() && !addQuestionContentFragment.getText().isEmpty());
            return;
        }
        if (addChoicesFragment != null && addChoicesFragment.isVisible()) {
            buttonLeft.setEnabled(true);
            buttonRight.setText(R.string.finish);
            buttonRight.setEnabled(addChoicesFragment.isAllFilled());
        }
    }

    private void commitToDataBase() {
        // TODO: 2016/2/22 use thread to do the following task
        String content = addQuestionContentFragment.getText();
        String choiceA = addChoicesFragment.getEditA();
        String choiceB = addChoicesFragment.getEditB();
        String choiceC = addChoicesFragment.getEditC();
        String choiceD = addChoicesFragment.getEditD();
        String answer = addChoicesFragment.getRadioChecked();
        DataBaseHelper helper = new DataBaseHelper(TeacherAddChoiceQuestionActivity.this, GlobalKeeper.DB_NAME, 1);
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("CONTENT", content);
        contentValues.put("CHOICEA", choiceA);
        contentValues.put("CHOICEB", choiceB);
        contentValues.put("CHOICEC", choiceC);
        contentValues.put("CHOICED", choiceD);
        contentValues.put("ANSWER", answer);
        db.insert("choiceQuestion", null, contentValues);
        db.close();
    }
}
