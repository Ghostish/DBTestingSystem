package com.bbt.kangel.dbtesingsystem.activity;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bbt.kangel.dbtesingsystem.R;
import com.bbt.kangel.dbtesingsystem.fragment.ConfirmAlertDialogFragment;
import com.bbt.kangel.dbtesingsystem.util.DataBaseHelper;
import com.bbt.kangel.dbtesingsystem.util.DialogActivity;
import com.bbt.kangel.dbtesingsystem.util.GlobalKeeper;
import com.bbt.kangel.dbtesingsystem.util.TestDataBaseUtil;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Kangel on 2016/2/13.
 */
public class TeacherMarkActivity extends AppCompatActivity implements View.OnClickListener, DialogActivity {

    private static final int CONFIRM_QUIT = 0;
    private static final int PROGRESS = 1;
    private static final int CONFIRM_SUBMIT = 2;
    private static final int PAPER_PREPARE = 223;
    private static final int COMMIT_FINISH = 242;
    private Cursor cursor;
    private SQLiteDatabase db;
    private String SNO;
    private Handler handler;
    private int PID;
    private ViewPager pager;
    private Toolbar toolbar;
    private String indicatorString;
    private MarkFragmentAdapter adapter;
    private ConfirmAlertDialogFragment confirmQuitDialog;
    private ConfirmAlertDialogFragment confirmSubmitDialog;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getIntent().getExtras();
        handler = new mHandler(TeacherMarkActivity.this);
        if (args != null) {
            SNO = args.getString("sno");
            PID = args.getInt("pid");
            mShowDialog(PROGRESS, getString(R.string.paper_loading));
            new Thread(new Runnable() {
                @Override
                public void run() {
                    DataBaseHelper dataBaseHelper = new DataBaseHelper(TeacherMarkActivity.this, GlobalKeeper.DB_NAME, 1);
                    db = dataBaseHelper.getWritableDatabase();
                    cursor = TestDataBaseUtil.getQuestionUnmarked(db, SNO, PID + "");
                    Log.d("count", cursor.getCount() + "");
                    Log.d("sno ,pid", SNO + " " + PID);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (handler != null) {
                        Message msg = Message.obtain(handler, PAPER_PREPARE);
                        handler.sendMessage(msg);
                    }
                }
            }).start();
        }
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submit_button: {
                mShowDialog(CONFIRM_SUBMIT, null);
                break;
            }
            default: {
                mShowDialog(CONFIRM_QUIT, null);
            }
        }
    }

    @Override
    public void onBackPressed() {
        mShowDialog(CONFIRM_QUIT, null);
    }

    @Override
    public void dismissDialog() {

    }

    @Override
    public void doAtPositiveButton(String tag) {
        switch (tag) {
            case "submitDialog": {
                mShowDialog(PROGRESS, getString(R.string.msg_saving));
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        submit();
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (handler != null) {
                            Message msg = Message.obtain(handler, COMMIT_FINISH);
                            handler.sendMessage(msg);
                        }
                    }
                }).start();
                break;
            }
            case "quitDialog": {
                finish();
                break;
            }
        }
    }

    @Override
    public void onDialogItemSelect(Bundle args) {

    }

    private void mShowDialog(int type, String msg) {
        switch (type) {
            case CONFIRM_QUIT:
                if (confirmQuitDialog == null) {
                    confirmQuitDialog = ConfirmAlertDialogFragment.newInstance(R.style.DialogStyle, getString(R.string.title_quit), getString(R.string.msg_quit_mark));
                }
                confirmQuitDialog.show(getSupportFragmentManager(), "quitDialog");
                break;
            case CONFIRM_SUBMIT:
                if (confirmSubmitDialog == null) {
                    confirmSubmitDialog = ConfirmAlertDialogFragment.newInstance(R.style.DialogStyle, getString(R.string.title_submit), getString(R.string.msg_submit_mark));
                }
                confirmSubmitDialog.show(getSupportFragmentManager(), "submitDialog");
                break;
            case PROGRESS:
                if (progressDialog == null) {
                    progressDialog = new ProgressDialog(TeacherMarkActivity.this);
                    progressDialog.setCancelable(false);
                }
                progressDialog.setMessage(msg);
                progressDialog.show();
                break;
        }
    }

    private void submit() {
        for (int i = 0; i < adapter.map.size(); i++) {
            if (adapter.getFragment(i) != null) {
                adapter.getFragment(i).commitToDataBase(this);
            }
        }
    }

    private void initView() {
        setContentView(R.layout.activity_marking);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(this);
        indicatorString = getString(R.string.mark_indicator);
        toolbar.setTitle(String.format(indicatorString, 1, cursor.getCount()));
        pager = (ViewPager) findViewById(R.id.pager);
        adapter = new MarkFragmentAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                toolbar.setTitle(String.format(indicatorString, position + 1, cursor.getCount()));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    static class mHandler extends Handler {
        private WeakReference<TeacherMarkActivity> mActivity;

        public mHandler(TeacherMarkActivity activity) {
            this.mActivity = new WeakReference<TeacherMarkActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            TeacherMarkActivity activity = mActivity.get();
            switch (msg.what) {
                case COMMIT_FINISH: {
                    activity.progressDialog.dismiss();
                    activity.finish();
                    break;
                }
                case PAPER_PREPARE: {
                    activity.initView();
                    activity.progressDialog.dismiss();
                    break;
                }
            }
        }

    }

    public static class MarkFragment extends Fragment {
        private int type;
        private int qid;
        private String markScoreIndicatorString;
        private AppCompatSeekBar scoreBar;

        static MarkFragment newInstance(Bundle args) {
            MarkFragment f = new MarkFragment();
            f.setArguments(args);
            f.type = args.getInt("type");
            f.qid = args.getInt("qid");
            return f;
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_mark, container, false);
            Bundle args = getArguments();
            markScoreIndicatorString = getString(R.string.score_indicator);
            if (args != null) {
                TextView contentText = (TextView) v.findViewById(R.id.content);
                final TextView scoreText = (TextView) v.findViewById(R.id.text_score);
                EditText answerEdit = (EditText) v.findViewById(R.id.students_answers);
                scoreBar = (AppCompatSeekBar) v.findViewById(R.id.seek_bar_set_score);
                contentText.setText(args.getString("content"));
                scoreBar.setMax(args.getInt("score"));
                answerEdit.setText(args.getString("answer"));
                scoreBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        scoreText.setText(String.format(markScoreIndicatorString, progress));
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });
            }
            return v;
        }

        void commitToDataBase(TeacherMarkActivity activity) {
            String tableName = type == GlobalKeeper.TYPE_GAP ? "gapAnswers" : "essayAnswers";
            int score = scoreBar.getProgress();
            ContentValues values = new ContentValues();
            values.put("ISMARKED", 1);
            values.put("SCORE", score);
            activity.db.update(tableName, values, "SNO = ? and PID = ? and QID = ?", new String[]{activity.SNO, activity.PID + "", qid + ""});
        }
    }

    class MarkFragmentAdapter extends FragmentStatePagerAdapter {
        private Map<Integer, MarkFragment> map = new HashMap<>();

        public MarkFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            cursor.moveToPosition(position);
            Bundle args = new Bundle();
            args.putString("content", cursor.getString(cursor.getColumnIndex("CONTENT")));
            args.putString("answer", cursor.getString(cursor.getColumnIndex("ANSWER")));
            args.putInt("score", cursor.getInt(cursor.getColumnIndex("SCORE")));
            args.putInt("type", cursor.getInt(cursor.getColumnIndex("TYPE")));
            args.putInt("qid", cursor.getInt(cursor.getColumnIndex("QID")));
            args.putInt("position", position);
            MarkFragment f = MarkFragment.newInstance(args);
            map.put(position, f);
            return f;
        }

        @Override
        public int getCount() {
            return cursor.getCount();
        }

        MarkFragment getFragment(int position) {
            return map.get(position);
        }
    }

}
