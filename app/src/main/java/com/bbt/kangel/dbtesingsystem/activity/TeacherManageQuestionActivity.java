package com.bbt.kangel.dbtesingsystem.activity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.bbt.kangel.dbtesingsystem.R;
import com.bbt.kangel.dbtesingsystem.util.DataBaseHelper;
import com.bbt.kangel.dbtesingsystem.util.GlobalKeeper;
import com.bbt.kangel.dbtesingsystem.util.TestDataBaseUtil;

/**
 * Created by Kangel on 2016/2/20.
 */
public class TeacherManageQuestionActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView choiceText, gapText, essayText;
    private String choiceString, gapString, essayString;
    private SQLiteDatabase db;
    private Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_view_question_abstract);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(this);

        choiceText = (TextView) findViewById(R.id.text_choice);
        gapText = (TextView) findViewById(R.id.text_gap);
        essayText = (TextView) findViewById(R.id.text_essay);

        choiceString = getString(R.string.label_choice);
        gapString = getString(R.string.label_gap);
        essayString = getString(R.string.label_essay);

        DataBaseHelper helper = new DataBaseHelper(TeacherManageQuestionActivity.this, GlobalKeeper.DB_NAME, 1);
        db = helper.getReadableDatabase();
        cursor = TestDataBaseUtil.getQuestionCount(db);
        cursor.moveToFirst();
        choiceText.setText(String.format(choiceString, cursor.getInt(cursor.getColumnIndex("COUNT"))));
        cursor.moveToNext();
        gapText.setText(String.format(gapString, cursor.getInt(cursor.getColumnIndex("COUNT"))));
        cursor.moveToNext();
        essayText.setText(String.format(essayString, cursor.getInt(cursor.getColumnIndex("COUNT"))));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db != null && db.isOpen()) {
            db.close();
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.card_choice: {
                Intent intent = new Intent(TeacherManageQuestionActivity.this, TeacherViewQuestionDetailActivity.class);
                Bundle args = new Bundle();
                args.putInt("TYPE", GlobalKeeper.TYPE_CHOICE);
                intent.putExtras(args);
                startActivityForResult(intent, GlobalKeeper.TYPE_CHOICE);
                break;
            }
            case R.id.card_gap: {
                Intent intent = new Intent(TeacherManageQuestionActivity.this, TeacherViewQuestionDetailActivity.class);
                Bundle args = new Bundle();
                args.putInt("TYPE", GlobalKeeper.TYPE_GAP);
                intent.putExtras(args);
                startActivityForResult(intent, GlobalKeeper.TYPE_GAP);
                break;
            }
            case R.id.card_essay: {
                Intent intent = new Intent(TeacherManageQuestionActivity.this, TeacherViewQuestionDetailActivity.class);
                Bundle args = new Bundle();
                args.putInt("TYPE", GlobalKeeper.TYPE_ESSAY);
                intent.putExtras(args);
                startActivityForResult(intent, GlobalKeeper.TYPE_ESSAY);
                break;
            }
            default: {
                finish();
            }
        }
    }
}
