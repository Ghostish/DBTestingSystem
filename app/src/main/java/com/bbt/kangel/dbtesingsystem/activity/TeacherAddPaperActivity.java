package com.bbt.kangel.dbtesingsystem.activity;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.View;

import com.bbt.kangel.dbtesingsystem.R;
import com.bbt.kangel.dbtesingsystem.fragment.AddPaperSchemeFragment;
import com.bbt.kangel.dbtesingsystem.fragment.PickQuestionFragment;
import com.bbt.kangel.dbtesingsystem.util.DataBaseHelper;
import com.bbt.kangel.dbtesingsystem.util.GlobalKeeper;
import com.bbt.kangel.dbtesingsystem.util.RecyclerViewActivity;
import com.bbt.kangel.dbtesingsystem.util.TestDataBaseUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Kangel on 2016/2/22.
 */
public class TeacherAddPaperActivity extends AppCompatActivity implements View.OnClickListener, FragmentManager.OnBackStackChangedListener, RecyclerViewActivity, TextWatcher {
    private AddPaperSchemeFragment addPaperSchemeFragment;
    private PickQuestionFragment pickChoices;
    private PickQuestionFragment pickGaps;
    private PickQuestionFragment pickEssays;
    private ProgressDialog progressDialog;
    private Cursor choiceCursor, gapCursor, essayCursor;
    private SQLiteDatabase db;
    private SparseIntArray choicesList = new SparseIntArray(), gapsList = new SparseIntArray(), essaysList = new SparseIntArray();

    private AppCompatButton buttonLeft, buttonRight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_teacher_add_wrapper_layout);

        if (savedInstanceState == null) {
            addPaperSchemeFragment = new AddPaperSchemeFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.wrapper_layout, addPaperSchemeFragment, "addScheme");
            ft.commit();
        }
        DataBaseHelper helper = new DataBaseHelper(TeacherAddPaperActivity.this, GlobalKeeper.DB_NAME, 1);
        db = helper.getWritableDatabase();
        getSupportFragmentManager().addOnBackStackChangedListener(this);
        buttonLeft = (AppCompatButton) findViewById(R.id.action_button_left);
        buttonRight = (AppCompatButton) findViewById(R.id.action_button_right);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (choiceCursor != null && !choiceCursor.isClosed()) {
            choiceCursor.close();
        }
        if (gapCursor != null && !gapCursor.isClosed()) {
            gapCursor.close();
        }
        if (essayCursor != null && !essayCursor.isClosed()) {
            essayCursor.close();
        }
        if (db != null && db.isOpen()) {
            db.close();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.action_button_right:
                if (moveToNextFragment()) {
                    break;
                }
                if (progressDialog == null) {
                    progressDialog = new ProgressDialog(TeacherAddPaperActivity.this);
                    progressDialog.setCancelable(false);
                }
                progressDialog.setMessage(getString(R.string.msg_saving));
                progressDialog.show();
                new SavePaperInfo().execute();
                break;
            case R.id.action_button_left:
                moveToPreviousFragment();
                break;
        }
    }

    @Override
    public void onBackStackChanged() {
        if (addPaperSchemeFragment != null && addPaperSchemeFragment.isVisible()) {
            buttonLeft.setEnabled(false);
            buttonRight.setText(R.string.next_step);
            buttonRight.setEnabled(addPaperSchemeFragment.getTotal() == 100);
            return;
        }
        if (pickChoices != null && pickChoices.isVisible()) {
            buttonLeft.setEnabled(true);
            buttonRight.setText(R.string.next_step);
            buttonRight.setEnabled((addPaperSchemeFragment != null ? addPaperSchemeFragment.getChoiceCount() : 0) == choicesList.size());
            return;
        }
        if (pickGaps != null && pickGaps.isVisible()) {
            buttonLeft.setEnabled(true);
            buttonRight.setText(R.string.next_step);
            buttonRight.setEnabled((addPaperSchemeFragment != null ? addPaperSchemeFragment.getGapCount() : 0) == gapsList.size());
            return;
        }
        if (pickEssays != null && pickEssays.isVisible()) {
            buttonLeft.setEnabled(true);
            buttonRight.setText(R.string.finish);
            buttonRight.setEnabled(false);
        }

    }

    @Override
    public void onRecyclerViewItemSelect(Bundle args, String tag,int position) {
        boolean isPicked = args.getBoolean("isPicked");
        switch (tag) {
            case "choices":
                if (isPicked) {
                    choicesList.put(position, position);
                } else {
                    choicesList.delete(position);
                }
                pickChoices.setTitleTextNum(choicesList.size(), addPaperSchemeFragment.getChoiceCount());
                buttonRight.setEnabled(choicesList.size() == addPaperSchemeFragment.getChoiceCount());
                break;
            case "gaps":
                if (isPicked) {
                    gapsList.put(position, position);
                } else {
                    gapsList.delete(position);
                }
                pickGaps.setTitleTextNum(gapsList.size(), addPaperSchemeFragment.getGapCount());
                buttonRight.setEnabled(gapsList.size() == addPaperSchemeFragment.getGapCount());
                break;
            case "essays":
                if (isPicked) {
                    essaysList.put(position, position);
                } else {
                    essaysList.delete(position);
                }
                pickEssays.setTitleTextNum(essaysList.size(), addPaperSchemeFragment.getEssayCount());
                buttonRight.setEnabled(essaysList.size() == addPaperSchemeFragment.getEssayCount());
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        buttonRight.setEnabled(s.toString().equals("100"));
    }

    @Override
    public void onBackPressed() {
        if (moveToPreviousFragment()) {
            return;
        }
        super.onBackPressed();
    }

    private String getInClause(ArrayList list) {
        String notInClause = list.toString();
        Log.i("notInClause", notInClause);
        notInClause = notInClause.replace('[', '(');
        notInClause = notInClause.replace(']', ')');
        return notInClause;
    }

    private void commitToDataBase() {
        ArrayList<Integer> choicesQIDList = new ArrayList<>();
        ArrayList<Integer> gapsQIDList = new ArrayList<>();
        ArrayList<Integer> essaysQIDList = new ArrayList<>();

        /*get questions QID , which is being added to paper*/
        for (int i = 0; i < choicesList.size(); i++) {
            choiceCursor.moveToPosition(choicesList.valueAt(i));
            choicesQIDList.add(choiceCursor.getInt(choiceCursor.getColumnIndex("QID")));
        }
        for (int i = 0; i < gapsList.size(); i++) {
            gapCursor.moveToPosition(gapsList.valueAt(i));
            gapsQIDList.add(gapCursor.getInt(gapCursor.getColumnIndex("QID")));
        }
        for (int i = 0; i < essaysList.size(); i++) {
            essayCursor.moveToPosition(essaysList.valueAt(i));
            essaysQIDList.add(essayCursor.getInt(essayCursor.getColumnIndex("QID")));
        }

        /*get in clause to use in the sql query*/
        String choicesQIDClause = getInClause(choicesQIDList);
        String gapsQIDClause = getInClause(gapsQIDList);
        String essaysQIDClause = getInClause(essaysQIDList);

        /*get questions' score*/
        int choiceScore = addPaperSchemeFragment.getChoiceScore();
        int gapScore = addPaperSchemeFragment.getGapScore();
        int essayScore = addPaperSchemeFragment.getEssayScore();

        String currentTime = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());

        /*insert new row to Papers table*/
        ContentValues values = new ContentValues();
        values.put("CREATETIME", currentTime);
        db.insert("Papers", null, values);
        //db.rawQuery("insert into Papers (CREATETIME) values (" + currentTime + ")", null);
        Cursor c = db.rawQuery("select PID from Papers order by PID desc limit 1", null);
        if (c.moveToNext()) {
            //int PID = 3;
            int PID = c.getInt(c.getColumnIndex("PID"));
            Log.d("PID", PID + "");
            c.close();
            db.execSQL("insert into choiceInPapers (PID,QID,SCORE)\n" +
                    "select " + PID + " PID, QID, " + choiceScore + " SCORE from choiceQuestion where QID in " + choicesQIDClause);
            db.execSQL("insert into gapInPapers (PID,QID,SCORE)\n" +
                    "select " + PID + " PID, QID, " + gapScore + " SCORE from gapQuestion where QID in " + gapsQIDClause);
            db.execSQL("insert into essayInPapers (PID,QID,SCORE)\n" +
                    "select " + PID + " PID, QID, " + essayScore + " SCORE from gapQuestion where QID in " + essaysQIDClause);
        }


    }

    private boolean moveToNextFragment() {
        if (addPaperSchemeFragment != null && addPaperSchemeFragment.isVisible()) {
            if (pickChoices == null) {
                choiceCursor = TestDataBaseUtil.getQuestionByTYPE(db, GlobalKeeper.TYPE_CHOICE, null);
                pickChoices = PickQuestionFragment.newInstance(GlobalKeeper.TYPE_CHOICE, choiceCursor, addPaperSchemeFragment.getChoiceCount());
            }
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            ft.hide(addPaperSchemeFragment);
            ft.add(R.id.wrapper_layout, pickChoices, "pickChoices");
            ft.addToBackStack(null);
            ft.commit();
            return true;
        }
        if (pickChoices != null && pickChoices.isVisible()) {
            if (pickGaps == null) {
                gapCursor = TestDataBaseUtil.getQuestionByTYPE(db, GlobalKeeper.TYPE_GAP, null);
                pickGaps = PickQuestionFragment.newInstance(GlobalKeeper.TYPE_GAP, gapCursor, addPaperSchemeFragment != null ? addPaperSchemeFragment.getGapCount() : 0);
            }
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            ft.hide(pickChoices);
            ft.add(R.id.wrapper_layout, pickGaps, "pickGaps");
            ft.addToBackStack(null);
            ft.commit();
            return true;
        }
        if (pickGaps != null && pickGaps.isVisible()) {
            if (pickEssays == null) {
                essayCursor = TestDataBaseUtil.getQuestionByTYPE(db, GlobalKeeper.TYPE_ESSAY, null);
                pickEssays = PickQuestionFragment.newInstance(GlobalKeeper.TYPE_ESSAY, essayCursor, addPaperSchemeFragment != null ? addPaperSchemeFragment.getEssayCount() : 0);
            }
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            ft.hide(pickGaps);
            ft.add(R.id.wrapper_layout, pickEssays, "pickEssays");
            ft.addToBackStack(null);
            ft.commit();
            return true;
        }
        return false;
    }

    private boolean moveToPreviousFragment() {
        if (pickChoices != null && pickChoices.isVisible()) {
            getSupportFragmentManager().popBackStack();
            choicesList.clear();
            return true;
        }
        if (pickGaps != null && pickGaps.isVisible()) {
            getSupportFragmentManager().popBackStack();
            gapsList.clear();
            return true;
        }
        if (pickEssays != null && pickEssays.isVisible()) {
            getSupportFragmentManager().popBackStack();
            essaysList.clear();
            return true;
        }
        return false;
    }

    private class SavePaperInfo extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            db.beginTransaction();
            try {
                commitToDataBase();
                db.setTransactionSuccessful();
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("databaseError", "transaction failed");
            } finally {
                db.endTransaction();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (progressDialog != null) {
                progressDialog.dismiss();
                setResult(RESULT_OK);
                TeacherAddPaperActivity.this.finish();
            }
        }
    }
}
