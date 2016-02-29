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

import com.bbt.kangel.dbtesingsystem.R;
import com.bbt.kangel.dbtesingsystem.adapter.PeopleListAdapter;
import com.bbt.kangel.dbtesingsystem.fragment.ChooseItemDialogFragment;
import com.bbt.kangel.dbtesingsystem.util.DataBaseHelper;
import com.bbt.kangel.dbtesingsystem.util.DialogActivity;
import com.bbt.kangel.dbtesingsystem.util.EmptyViewRecyclerView;
import com.bbt.kangel.dbtesingsystem.util.GlobalKeeper;
import com.bbt.kangel.dbtesingsystem.util.ItemTouchHelperActivity;
import com.bbt.kangel.dbtesingsystem.util.RecyclerViewActivity;
import com.bbt.kangel.dbtesingsystem.util.SimpleItemTouchHelperCallback;
import com.bbt.kangel.dbtesingsystem.util.TestDataBaseUtil;

import java.util.ArrayList;

/**
 * Created by Kangel on 2016/2/18.
 */
public class DeanManageActivity extends AppCompatActivity implements View.OnClickListener, ItemTouchHelperActivity, RecyclerViewActivity, DialogActivity {
    private SQLiteDatabase db;
    private Cursor cursor;
    private PeopleListAdapter adapter;
    private CoordinatorLayout coordinatorLayout;
    private ParamOnClickListener paramOnClickListener;
    private ArrayList<String> deleteList = new ArrayList<>();
    private ChooseItemDialogFragment chooseItemDialogFragment;
    private int changedPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_list_with_fab);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        paramOnClickListener = new ParamOnClickListener();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.view_people_list);
        toolbar.setNavigationOnClickListener(this);

        DataBaseHelper helper = new DataBaseHelper(DeanManageActivity.this, GlobalKeeper.DB_NAME, 1);
        db = helper.getReadableDatabase();
        cursor = TestDataBaseUtil.getPeopleList(db, null);
        adapter = new PeopleListAdapter(DeanManageActivity.this, cursor);
        EmptyViewRecyclerView rv = (EmptyViewRecyclerView) findViewById(R.id.recycle_view);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);
        rv.setEmptyView(findViewById(R.id.empty_view));

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(DeanManageActivity.this);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(rv);

    }

    @Override
    protected void onDestroy() {
        // TODO: 2016/2/20 use thread to do the following task
        super.onDestroy();
        String inClause = deleteList.toString();
        Log.i("inClause", inClause);
        inClause = inClause.replace('[', '(');
        inClause = inClause.replace(']', ')');
        try {
            db.delete("students", "SNO in " + inClause, null);
            db.delete("teachers", "TNO in " + inClause, null);
        } catch (Exception e) {
            Log.e("sqlite", e.toString() + " delete failed");
        }
        if (db != null && db.isOpen()) {
            db.close();
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case DeanAddOrEditAccountActivity.MODE_ADD: {
                    updateAdapter();
                    adapter.notifyItemInserted(adapter.getItemCount() - 1);
                }
                case DeanAddOrEditAccountActivity.MODE_EDIT: {
                    updateAdapter();
                    adapter.notifyItemChanged(changedPosition);
                }
            }
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab: {
                if (chooseItemDialogFragment == null) {
                    chooseItemDialogFragment = ChooseItemDialogFragment.newInstance(R.array.add_new_account);
                }
                chooseItemDialogFragment.show(getSupportFragmentManager(), "chooseWhich");
                break;
            }
            default: {
                finish();
            }
        }
    }

    @Override
    public void onItemSwiped(int position) {
       /* final int position = args.getInt("position");
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
        Snackbar.make(coordinatorLayout, "已删除", Snackbar.LENGTH_LONG).setAction("撤销", paramOnClickListener).show();*/
        cursor.moveToPosition(position);
        String id = cursor.getString(cursor.getColumnIndex("ID"));
        deleteList.add(deleteList.size(), "'" + id + "'");
        updateAdapter();
        adapter.notifyItemRemoved(position);
        paramOnClickListener.setPosition(position);
        Snackbar.make(coordinatorLayout, "已删除", Snackbar.LENGTH_LONG).setAction("撤销", paramOnClickListener).show();
    }

    @Override
    public void onRecyclerViewItemSelect(Bundle args, String tag, int position) {
        changedPosition = position;
        Intent intent = new Intent(DeanManageActivity.this, DeanViewPeopleDetailActivity.class);
        intent.putExtras(args);
        startActivityForResult(intent, DeanAddOrEditAccountActivity.MODE_EDIT);

    }

    @Override
    public void dismissDialog() {

    }

    @Override
    public void doAtPositiveButton(String tag) {

    }

    @Override
    public void onDialogItemSelect(String tag, Bundle args) {
        if ("chooseWhich".equals(tag)) {
            Intent intent = new Intent(DeanManageActivity.this, DeanAddOrEditAccountActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("MODE", DeanAddOrEditAccountActivity.MODE_ADD);
            switch (args.getInt("which")) {
                case 0: {
                    bundle.putInt("TYPE", GlobalKeeper.TYPE_STUDENT);
                    intent.putExtras(bundle);
                    startActivityForResult(intent, DeanAddOrEditAccountActivity.MODE_ADD);
                    overridePendingTransition(0, 0);
                    break;
                }
                case 1:
                    bundle.putInt("TYPE", GlobalKeeper.TYPE_TEACHER);
                    intent.putExtras(bundle);
                    startActivityForResult(intent, DeanAddOrEditAccountActivity.MODE_ADD);
                    overridePendingTransition(0, 0);
                    break;
            }
        }
    }

    public class ParamOnClickListener implements View.OnClickListener {
        int position;

        public void setPosition(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            deleteList.remove(deleteList.size() - 1);
            updateAdapter();
            adapter.notifyItemInserted(position);
        }
    }

    private void updateAdapter() {
        cursor.close();
        cursor = TestDataBaseUtil.getPeopleList(db, getNotInClause());
        adapter.updateCursor(cursor);
    }

    private String getNotInClause() {
        String inClause = deleteList.toString();
        Log.i("inClause", inClause);
        inClause = inClause.replace('[', '(');
        return inClause.replace(']', ')');
    }
}
