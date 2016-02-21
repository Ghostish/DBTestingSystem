package com.bbt.kangel.dbtesingsystem.activity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.bbt.kangel.dbtesingsystem.R;
import com.bbt.kangel.dbtesingsystem.adapter.ViewQuestionListAdapter;
import com.bbt.kangel.dbtesingsystem.util.DataBaseHelper;
import com.bbt.kangel.dbtesingsystem.util.GlobalKeeper;
import com.bbt.kangel.dbtesingsystem.util.TestDataBaseUtil;

/**
 * Created by Kangel on 2016/2/21.
 */
public class TeacherViewQuestionDetailActivity extends AppCompatActivity implements View.OnClickListener {
    private int viewtype;
    private SQLiteDatabase db;
    private Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getIntent().getExtras();
        try {
            viewtype = args.getInt("TYPE");
        } catch (Exception e) {
            throw new NullPointerException(this.toString() + "must call this activity with TYPE set in bundle");
        }
        setContentView(R.layout.activity_teacher_paper_question_management);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(this);
        switch (viewtype) {
            case GlobalKeeper.TYPE_CHOICE:
                toolbar.setTitle(R.string.title_choice);
                break;
            case GlobalKeeper.TYPE_GAP:
                toolbar.setTitle(R.string.title_gap);
                break;
            case GlobalKeeper.TYPE_ESSAY:
                toolbar.setTitle(R.string.title_essay);
                break;
        }

        DataBaseHelper helper = new DataBaseHelper(TeacherViewQuestionDetailActivity.this, GlobalKeeper.DB_NAME, 1);
        db = helper.getWritableDatabase();
        cursor = TestDataBaseUtil.getQuestionByTYPE(db, viewtype);

        RecyclerView rv = (RecyclerView) findViewById(R.id.recycle_view);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(this));
        ViewQuestionListAdapter adapter = new ViewQuestionListAdapter(TeacherViewQuestionDetailActivity.this, cursor, false,false);
        rv.setAdapter(adapter);
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
            case R.id.fab: {
                if (viewtype == GlobalKeeper.TYPE_CHOICE) {
                    // TODO: 2016/2/21 start add new choice question activity
                } else {
                    Intent intent = new Intent(TeacherViewQuestionDetailActivity.this, TeacherAddNewTextQuestionActivity.class);
                    startActivity(intent);
                    break;
                }
            }
            default: {
                finish();
            }
        }
    }
}
