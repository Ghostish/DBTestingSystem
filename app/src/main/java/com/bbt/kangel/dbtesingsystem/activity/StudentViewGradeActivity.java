package com.bbt.kangel.dbtesingsystem.activity;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.bbt.kangel.dbtesingsystem.R;
import com.bbt.kangel.dbtesingsystem.util.TestDataBaseUtil;
import com.bbt.kangel.dbtesingsystem.util.DataBaseHelper;

import com.bbt.kangel.dbtesingsystem.adapter.StudentViewGradeAdapter;

/**
 * Created by Kangel on 2015/12/15.
 */
public class StudentViewGradeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_grade);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycle_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        DataBaseHelper helper = new DataBaseHelper(this,"test.db",1);
        String SNO = PreferenceManager.getDefaultSharedPreferences(this).getString("ID",null);
        StudentViewGradeAdapter adapter = new StudentViewGradeAdapter(this, TestDataBaseUtil.getGradeDetail(helper.getReadableDatabase(),SNO));
        recyclerView.setAdapter(adapter);
    }
}
