package com.bbt.kangel.dbtesingsystem.activity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.bbt.kangel.dbtesingsystem.R;
import com.bbt.kangel.dbtesingsystem.util.GlobalKeeper;
import com.bbt.kangel.dbtesingsystem.util.TestDataBaseUtil;
import com.bbt.kangel.dbtesingsystem.util.DataBaseHelper;

import com.bbt.kangel.dbtesingsystem.adapter.StudentViewGradeAdapter;

/**
 * Created by Kangel on 2015/12/15.
 */
public class StudentViewGradeActivity extends AppCompatActivity {
    private SQLiteDatabase db;
    private Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycle_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        DataBaseHelper helper = new DataBaseHelper(this, GlobalKeeper.DB_NAME, 1);
        db = helper.getReadableDatabase();
        String SNO = PreferenceManager.getDefaultSharedPreferences(this).getString("ID", null);
        cursor = TestDataBaseUtil.getGradeDetail(db, SNO);
        StudentViewGradeAdapter adapter = new StudentViewGradeAdapter(this, cursor);
        recyclerView.setAdapter(adapter);
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
}
