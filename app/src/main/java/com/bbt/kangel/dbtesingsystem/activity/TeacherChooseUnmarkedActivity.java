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
import com.bbt.kangel.dbtesingsystem.util.RecyclerViewActivity;
import com.bbt.kangel.dbtesingsystem.util.TestDataBaseUtil;
import com.bbt.kangel.dbtesingsystem.util.DataBaseHelper;

/**
 * Created by Kangel on 2016/2/12.
 */
public class TeacherChooseUnmarkedActivity extends AppCompatActivity implements RecyclerViewActivity {
    private SQLiteDatabase db;
    private Cursor c;
    private int PID;
    private TeacherViewGradeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_view_unmarked_paper);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            PID = bundle.getInt("PID");
            RecyclerView rv = (RecyclerView) findViewById(R.id.recycle_view);
            rv.setHasFixedSize(true);
            rv.setLayoutManager(new LinearLayoutManager(this));
            DataBaseHelper helper = new DataBaseHelper(this, GlobalKeeper.DB_NAME, 1);
            db = helper.getReadableDatabase();
            c = TestDataBaseUtil.getPaperUnmarked(db, PID);
            adapter = new TeacherViewGradeAdapter(this, c);
            rv.setAdapter(adapter);
        }

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        c.close();
        c = TestDataBaseUtil.getPaperUnmarked(db, PID);
        adapter.updataCursor(c);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (c != null && !c.isClosed()) {
            c.close();
        }
        if (db != null && db.isOpen()) {
            db.close();
        }
    }

    @Override
    public void onRecyclerViewItemSelect(Bundle args) {
        Intent intent = new Intent(TeacherChooseUnmarkedActivity.this, TeacherMarkActivity.class);
        intent.putExtras(args);
        startActivity(intent);
    }
}
