package com.bbt.kangel.dbtesingsystem.activity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.bbt.kangel.dbtesingsystem.R;
import com.bbt.kangel.dbtesingsystem.adapter.TeacherViewGradeAdapter;
import com.bbt.kangel.dbtesingsystem.util.GlobalKeeper;
import com.bbt.kangel.dbtesingsystem.util.TestDataBaseUtil;
import com.bbt.kangel.dbtesingsystem.util.mDataBaseHelper;

/**
 * Created by Kangel on 2016/2/12.
 */
public class TeacherChooseUnmarkedActivity extends AppCompatActivity {
    private SQLiteDatabase db;
    private Cursor c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_view_unmarked_paper);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            int PID = bundle.getInt("PID");
            RecyclerView rv = (RecyclerView) findViewById(R.id.recycle_view);
            rv.setHasFixedSize(true);
            rv.setLayoutManager(new LinearLayoutManager(this));
            mDataBaseHelper helper = new mDataBaseHelper(this, GlobalKeeper.DB_NAME, 1);
            db = helper.getReadableDatabase();
            Cursor c = TestDataBaseUtil.getPaperUnmarked(db,PID);
            TeacherViewGradeAdapter adapter = new TeacherViewGradeAdapter(this,c);
            rv.setAdapter(adapter);
        }

    }
}
