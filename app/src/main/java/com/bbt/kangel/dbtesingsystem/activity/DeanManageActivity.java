package com.bbt.kangel.dbtesingsystem.activity;

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
import com.bbt.kangel.dbtesingsystem.adapter.PeopleListAdapter;
import com.bbt.kangel.dbtesingsystem.util.DataBaseHelper;
import com.bbt.kangel.dbtesingsystem.util.GlobalKeeper;
import com.bbt.kangel.dbtesingsystem.util.ItemTouchHelperActivity;
import com.bbt.kangel.dbtesingsystem.util.SimpleItemTouchHelperCallback;
import com.bbt.kangel.dbtesingsystem.util.TestDataBaseUtil;

import java.util.ArrayList;

/**
 * Created by Kangel on 2016/2/18.
 */
public class DeanManageActivity extends AppCompatActivity implements View.OnClickListener, ItemTouchHelperActivity {
    private SQLiteDatabase db;
    private Cursor cursor;
    private PeopleListAdapter adapter;
    private CoordinatorLayout coordinatorLayout;
    private ParamOnClickListener paramOnClickListener;
    private ArrayList<String> deleteList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_list);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        paramOnClickListener = new ParamOnClickListener();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.view_people_list));
        toolbar.setNavigationOnClickListener(this);

        DataBaseHelper helper = new DataBaseHelper(DeanManageActivity.this, GlobalKeeper.DB_NAME, 1);
        db = helper.getReadableDatabase();
        cursor = TestDataBaseUtil.getPeopleList(db, null);
        adapter = new PeopleListAdapter(DeanManageActivity.this, cursor);
        RecyclerView rv = (RecyclerView) findViewById(R.id.recycle_view);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(rv);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        String inClause = deleteList.toString();
        Log.i("inClause", inClause);
        inClause = inClause.replace('[', '(');
        inClause = inClause.replace(']', ')');
        db.delete("students", "SNO in " + inClause, null);
        db.delete("teachers", "TNO in " + inClause, null);
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
            default: {
                finish();
            }
        }
    }


    @Override
    public void onItemSwiped(Bundle args) {
        final int position = args.getInt("position");
        String id = args.getString("id");
        deleteList.add(deleteList.size(), id);
        String inClause = deleteList.toString();
        Log.i("inClause", inClause);
        inClause = inClause.replace('[', '(');
        inClause = inClause.replace(']', ')');
        cursor.close();
        cursor = TestDataBaseUtil.getPeopleList(db, inClause);
        adapter.updateCursor(cursor);
        adapter.notifyItemRemoved(position);
        paramOnClickListener.setPosition(position);
        Snackbar.make(coordinatorLayout, "已删除", Snackbar.LENGTH_LONG).setAction("撤销", paramOnClickListener).show();
    }

    public class ParamOnClickListener implements View.OnClickListener {
        int position;

        public void setPosition(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            deleteList.remove(deleteList.size() - 1);
            String inClause = deleteList.toString();
            Log.i("inClause", inClause);
            inClause = inClause.replace('[', '(');
            inClause = inClause.replace(']', ')');
            cursor.close();
            cursor = TestDataBaseUtil.getPeopleList(db, inClause);
            adapter.updateCursor(cursor);
            adapter.notifyItemInserted(position);
        }
    }
}
