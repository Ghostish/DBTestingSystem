package com.bbt.kangel.dbtesingsystem.activity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;

import com.bbt.kangel.dbtesingsystem.R;
import com.bbt.kangel.dbtesingsystem.adapter.ViewPaperListAdapter;
import com.bbt.kangel.dbtesingsystem.util.DataBaseHelper;
import com.bbt.kangel.dbtesingsystem.util.GlobalKeeper;
import com.bbt.kangel.dbtesingsystem.util.ItemTouchHelperActivity;
import com.bbt.kangel.dbtesingsystem.util.RecyclerViewActivity;
import com.bbt.kangel.dbtesingsystem.util.SimpleItemTouchHelperCallback;
import com.bbt.kangel.dbtesingsystem.util.TestDataBaseUtil;

import java.util.ArrayList;

/**
 * Created by Kangel on 2016/2/20.
 */
public class TeacherManagePaperActivity extends AppCompatActivity implements View.OnClickListener, RecyclerViewActivity, ItemTouchHelperActivity {
    private SQLiteDatabase db;
    private Cursor c;
    private ArrayList<Integer> deleteList = new ArrayList<>();
    private ViewPaperListAdapter adapter;
    private ParamOnClickListener paramOnClickListener;

    private CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_list_with_fab);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout)
        ;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(this);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycle_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(TeacherManagePaperActivity.this));
        DataBaseHelper helper = new DataBaseHelper(TeacherManagePaperActivity.this, GlobalKeeper.DB_NAME, 1);
        db = helper.getWritableDatabase();
        c = TestDataBaseUtil.getPaperList(db, null);
        adapter = new ViewPaperListAdapter(TeacherManagePaperActivity.this, c);
        recyclerView.setAdapter(adapter);

        SimpleItemTouchHelperCallback callback = new SimpleItemTouchHelperCallback(TeacherManagePaperActivity.this);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        paramOnClickListener = new ParamOnClickListener();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab: {
                Intent intent = new Intent(TeacherManagePaperActivity.this, TeacherAddPaperActivity.class);
                startActivityForResult(intent, 0);
                break;
            }
            default: {
                Log.i("id", v.getId() + "");
                finish();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            notifyRecyclerViewDataChanged();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // TODO: 2016/2/23 use thread to do the following task
        try {
            db.delete("Papers", "PID in" + getNotInClause(), null);
        } catch (Exception e) {
            Log.e("sqlite", e.toString() + " delete failed due to foreign key constraint");
        }
        if (db != null && db.isOpen()) {
            db.close();
        }
        if (c != null && !c.isClosed()) {
            c.close();
        }
    }

    @Override
    public void onRecyclerViewItemSelect(Bundle args, String tag,int position) {
        Intent intent = new Intent(TeacherManagePaperActivity.this, TeacherViewPaperDetailActivity.class);
        intent.putExtras(args);
        startActivity(intent);
    }

    private void notifyRecyclerViewDataChanged() {
        c.close();
        c = TestDataBaseUtil.getPaperList(db, getNotInClause());
        adapter.updateCursor(c);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onItemSwiped(int position) {
        c.moveToPosition(position);
        int PID = c.getInt(c.getColumnIndex("PID"));
        deleteList.add(deleteList.size(), PID);
        c.close();
        c = TestDataBaseUtil.getPaperList(db, getNotInClause());
        adapter.updateCursor(c);
        adapter.notifyItemRemoved(position);
        paramOnClickListener.setPosition(position);
        Snackbar.make(coordinatorLayout, "已删除", Snackbar.LENGTH_LONG).setAction("撤销", paramOnClickListener).show();
    }

    private String getNotInClause() {
        String notInClause = deleteList.toString();
        Log.i("notInClause", notInClause);
        notInClause = notInClause.replace('[', '(');
        notInClause = notInClause.replace(']', ')');
        return notInClause;
    }

    private class ParamOnClickListener implements View.OnClickListener {
        int position;

        public void setPosition(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            deleteList.remove(deleteList.size() - 1);
            c.close();
            c = TestDataBaseUtil.getPaperList(db,getNotInClause());
            adapter.updateCursor(c);
            adapter.notifyItemInserted(position);
        }
    }
}
