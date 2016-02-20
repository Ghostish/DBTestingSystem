package com.bbt.kangel.dbtesingsystem.activity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.bbt.kangel.dbtesingsystem.R;
import com.bbt.kangel.dbtesingsystem.adapter.ViewQuestionListAdapter;
import com.bbt.kangel.dbtesingsystem.util.DataBaseHelper;
import com.bbt.kangel.dbtesingsystem.util.GlobalKeeper;
import com.bbt.kangel.dbtesingsystem.util.TestDataBaseUtil;

/**
 * Created by Kangel on 2016/2/20.
 */
public class TeacherViewPaperDetailActivity extends AppCompatActivity implements View.OnClickListener {
    private SQLiteDatabase db;
    private Cursor cursor;
    private String PID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getIntent().getExtras();
        try {
            PID = args.getString("PID");
        } catch (Exception e) {
            throw new NullPointerException(this.toString() + "must call this activity with PID set in bundle");
        }
        setContentView(R.layout.activity_view_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_view_paper_detail);
        toolbar.setNavigationOnClickListener(this);

        RecyclerView rv = (RecyclerView) findViewById(R.id.recycle_view);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(this));
        DataBaseHelper helper = new DataBaseHelper(TeacherViewPaperDetailActivity.this, GlobalKeeper.DB_NAME, 1);
        db = helper.getReadableDatabase();
        cursor = TestDataBaseUtil.getQuestionsByQID(db, PID);
        ViewQuestionListAdapter adapter = new ViewQuestionListAdapter(TeacherViewPaperDetailActivity.this, cursor,false);
        rv.setAdapter(adapter);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:{
                finish();
            }
        }
    }
}
