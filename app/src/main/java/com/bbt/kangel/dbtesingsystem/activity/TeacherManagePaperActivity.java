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
import com.bbt.kangel.dbtesingsystem.adapter.ViewPaperListAdapter;
import com.bbt.kangel.dbtesingsystem.util.DataBaseHelper;
import com.bbt.kangel.dbtesingsystem.util.GlobalKeeper;
import com.bbt.kangel.dbtesingsystem.util.RecyclerViewActivity;
import com.bbt.kangel.dbtesingsystem.util.TestDataBaseUtil;

/**
 * Created by Kangel on 2016/2/20.
 */
public class TeacherManagePaperActivity extends AppCompatActivity implements View.OnClickListener ,RecyclerViewActivity{
    private SQLiteDatabase db;
    private Cursor c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_paper_question_management);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(this);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycle_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(TeacherManagePaperActivity.this));
        DataBaseHelper helper = new DataBaseHelper(TeacherManagePaperActivity.this, GlobalKeeper.DB_NAME, 1);
        db = helper.getWritableDatabase();
        c = TestDataBaseUtil.getPaperList(db);
        ViewPaperListAdapter adapter = new ViewPaperListAdapter(TeacherManagePaperActivity.this, c);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default: {
                Log.i("id", v.getId() + "");
                finish();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(db != null &&db.isOpen()){
            db.close();
        }
        if(c != null && !c.isClosed()){
            c.close();
        }
    }

    @Override
    public void onRecyclerViewItemSelect(Bundle args) {
        Intent intent = new Intent(TeacherManagePaperActivity.this, TeacherViewPaperDetailActivity.class);
        intent.putExtras(args);
        startActivity(intent);
    }
}
