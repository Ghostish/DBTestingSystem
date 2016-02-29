package com.bbt.kangel.dbtesingsystem.activity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bbt.kangel.dbtesingsystem.R;
import com.bbt.kangel.dbtesingsystem.adapter.ViewQuestionListAdapter;
import com.bbt.kangel.dbtesingsystem.util.DataBaseHelper;
import com.bbt.kangel.dbtesingsystem.util.EmptyViewRecyclerView;
import com.bbt.kangel.dbtesingsystem.util.GlobalKeeper;
import com.bbt.kangel.dbtesingsystem.util.ItemTouchHelperActivity;
import com.bbt.kangel.dbtesingsystem.util.SimpleItemTouchHelperCallback;
import com.bbt.kangel.dbtesingsystem.util.TestDataBaseUtil;

import java.util.ArrayList;

/**
 * Created by Kangel on 2016/2/21.
 */
public class TeacherViewQuestionDetailActivity extends AppCompatActivity implements View.OnClickListener, ItemTouchHelperActivity {
    private int viewType;
    private SQLiteDatabase db;
    private Cursor cursor;
    private boolean hasQuestionAdded = false;
    private ViewQuestionListAdapter adapter;
    private ArrayList<String> deleteList = new ArrayList<>();
    private CoordinatorLayout coordinatorLayout;
    private ParamOnClickListener paramOnClickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getIntent().getExtras();
        try {
            viewType = args.getInt("TYPE");
        } catch (Exception e) {
            throw new NullPointerException(this.toString() + "must call this activity with TYPE set in bundle");
        }
        setContentView(R.layout.activity_view_list_with_fab);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        paramOnClickListener = new ParamOnClickListener();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(this);
        switch (viewType) {
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
        cursor = TestDataBaseUtil.getQuestionByTYPE(db, viewType, null);

        EmptyViewRecyclerView rv = (EmptyViewRecyclerView) findViewById(R.id.recycle_view);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ViewQuestionListAdapter(TeacherViewQuestionDetailActivity.this, cursor, false, false);
        rv.setAdapter(adapter);
        rv.setEmptyView(findViewById(R.id.empty_view));

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(TeacherViewQuestionDetailActivity.this);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(rv);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // TODO: 2016/2/21 use thread to do the following task
        Log.d("cycle", "onDestroy");
        if (db != null && db.isOpen()) {
            db.close();
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
    }

    @Override
    public void onBackPressed() {
        if (hasQuestionAdded || !deleteList.isEmpty()) {
            Log.d("ifresult", "fsfsfsfs");
            setResult(RESULT_OK);
        }
        super.onBackPressed();
    }

    @Override
    public void finish() {
        /*delete from database*/
        if (!deleteList.isEmpty()) {
            String tableName = null;
            switch (viewType) {
                case GlobalKeeper.TYPE_CHOICE:
                    tableName = "choiceQuestion";
                    break;
                case GlobalKeeper.TYPE_GAP:
                    tableName = "gapQuestion";
                    break;
                case GlobalKeeper.TYPE_ESSAY:
                    tableName = "essayQuestion";
                    break;
            }
            try {
                db.delete(tableName, "QID in" + getNotInClause(), null);
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), R.string.delete_failed_due_to_foreign_key_constraint, Toast.LENGTH_LONG).show();
                Log.e("sqlite", e.toString() + " delete failed due to foreign key constraint");
            }
        }
        super.finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            hasQuestionAdded = true;
            notifyRecyclerViewDataChanged();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab: {
                if (viewType == GlobalKeeper.TYPE_CHOICE) {
                    // TODO: 2016/2/21 start add new choice question activity
                    Intent intent = new Intent(TeacherViewQuestionDetailActivity.this, TeacherAddChoiceQuestionActivity.class);
                    Bundle args = new Bundle();
                    args.putInt("TYPE", viewType);
                    intent.putExtras(args);
                    startActivityForResult(intent, viewType);
                } else {
                    Intent intent = new Intent(TeacherViewQuestionDetailActivity.this, TeacherAddNewTextQuestionActivity.class);
                    Bundle args = new Bundle();
                    args.putInt("TYPE", viewType);
                    intent.putExtras(args);
                    startActivityForResult(intent, viewType);
                }
                break;
            }
            default: {
                if (hasQuestionAdded || !deleteList.isEmpty()) {
                    setResult(RESULT_OK);
                }
                finish();
            }
        }
    }

    @Override
    public void onItemSwiped(int position) {
        cursor.moveToPosition(position);
        String qid = cursor.getString(cursor.getColumnIndex("QID"));
        deleteList.add(deleteList.size(), qid);
        cursor.close();
        cursor = TestDataBaseUtil.getQuestionByTYPE(db, viewType, getNotInClause());
        adapter.updateCursor(cursor);
        adapter.notifyItemRemoved(position);
        paramOnClickListener.setPosition(position);
        Snackbar.make(coordinatorLayout, "已删除", Snackbar.LENGTH_LONG).setAction("撤销", paramOnClickListener).show();
    }

    private void notifyRecyclerViewDataChanged() {
        cursor.close();
        cursor = TestDataBaseUtil.getQuestionByTYPE(db, viewType, getNotInClause());
        adapter.updateCursor(cursor);
        adapter.notifyDataSetChanged();
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
            cursor.close();
            cursor = TestDataBaseUtil.getQuestionByTYPE(db, viewType, getNotInClause());
            adapter.updateCursor(cursor);
            adapter.notifyItemInserted(position);
        }
    }
}
