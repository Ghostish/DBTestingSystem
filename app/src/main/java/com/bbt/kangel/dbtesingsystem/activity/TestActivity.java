package com.bbt.kangel.dbtesingsystem.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bbt.kangel.dbtesingsystem.R;
import com.bbt.kangel.dbtesingsystem.fragment.ConfirmAlertDialogFragment;
import com.bbt.kangel.dbtesingsystem.fragment.GotoQuestionDialogFragment;
import com.bbt.kangel.dbtesingsystem.util.DataBaseHelper;
import com.bbt.kangel.dbtesingsystem.util.DialogActivity;
import com.bbt.kangel.dbtesingsystem.util.GlobalKeeper;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Kangel on 2015/12/12.
 */
public class TestActivity extends AppCompatActivity implements View.OnClickListener, DialogActivity {
    private final static String SELECT_CHOICES = "select choiceQuestion.QID QID,CONTENT,CHOICEA ,CHOICEB,CHOICEC,CHOICED ,ANSWER,SCORE from choiceQuestion,choiceInPapers where choiceInPapers.PID = ? and choiceQuestion.QID = choiceInPapers.QID";
    private final static String SELECT_GAPS = "select gapQuestion.QID QID,CONTENT from gapQuestion,gapInPapers where gapInPapers.PID = ? and gapQuestion.QID = gapInPapers.QID";
    private final static String SELECT_ESSAY = "select essayQuestion.QID QID,CONTENT from essayQuestion,essayInPapers where essayInPapers.PID = ? and essayQuestion.QID = essayInPapers.QID";
    private Cursor choiceCursor, gapCursor, essayCursor;
    private int choiceCount, gapCount, essayCount;
    private ViewPager pager;
    private TextView timeLast;
    private Timer timer = null;
    private TimerTask timerTask = null;
    static private Handler handler = null;
    private final static int UPDATE_TIME_LAST = 221, COMMIT_FINISHED = 2121, PAPER_PREPARED = 1212;
    private final static int PROGRESS = 0, CONFIRM_QUIT = 1, CHOOSE = 2, CONFIRM_SUBMIT = 3;
    private int count = 5400;
    final static private int PERIOD = 1000;
    private TestAdapter mAdapter;
    private DataBaseHelper helper;
    private SQLiteDatabase db;
    private String PID;
    private String SNO;
    private ProgressDialog progressDialog;
    private ConfirmAlertDialogFragment confirmQuitDialog, confirmSubmitDialog;
    private GotoQuestionDialogFragment gotoQuestionDialogFragment;
    private boolean[] questionTested;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_test);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            PID = bundle.getString("PID");
        } else {
            Log.e("error", "no valid paper id");
            this.finish();
        }
        handler = new mHandler(TestActivity.this);
        mShowDialog(PROGRESS, getResources().getString(R.string.paper_loading));
        new Thread(new Runnable() {
            @Override
            public void run() {
                helper = new DataBaseHelper(TestActivity.this, GlobalKeeper.DB_NAME, 1);
                db = helper.getWritableDatabase();
                choiceCursor = db.rawQuery(SELECT_CHOICES, new String[]{PID + ""});
                gapCursor = db.rawQuery(SELECT_GAPS, new String[]{PID + ""});
                essayCursor = db.rawQuery(SELECT_ESSAY, new String[]{PID + ""});
                choiceCount = choiceCursor.getCount();
                gapCount = gapCursor.getCount();
                essayCount = essayCursor.getCount();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (handler != null) {
                    Message message = Message.obtain(handler, PAPER_PREPARED);
                    handler.sendMessage(message);
                }
            }
        }).start();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(this);

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
    public void onBackPressed() {
        mShowDialog(CONFIRM_QUIT, null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.previous_button:
                if (pager.getCurrentItem() >= 1) {
                    pager.setCurrentItem(pager.getCurrentItem() - 1);
                }
                break;
            case R.id.next_button:
                if (pager.getCurrentItem() < choiceCount + gapCount + essayCount - 1) {
                    pager.setCurrentItem(pager.getCurrentItem() + 1);
                }
                break;
            case R.id.submit_button: {
                // TODO: 2016/2/20 use questionTested array to get a more accurate result
                int leftToDo = mAdapter.getCount() - mAdapter.getMapSize();
                String msg = "";
                if (leftToDo != 0) {
                    msg += "你还有" + leftToDo + "道题没做答,";
                }
                msg += getString(R.string.submit_alert_msg);
                mShowDialog(CONFIRM_SUBMIT, msg);
            }
            break;
            case R.id.goto_edit: {
                mShowDialog(CHOOSE, null);
                break;
            }
            case R.id.question_num:
                int nextPosition = Integer.parseInt(((TextView) v).getText().toString()) - 1;
                gotoQuestionDialogFragment.dismiss();
                pager.setCurrentItem(nextPosition, true);
                break;
            default://for navigation button
            {
                mShowDialog(CONFIRM_QUIT, null);
                /*boolean[] data = {false, false, true, false, true, false, false, false, true, false, true, false, false, false, true, false, true, false, false, false, true, false, true, false, false, false, true, false, true, false};
                GotoQuestionDialogFragment gotoQuestionDialogFragment = GotoQuestionDialogFragment.newInstance(data);
                gotoQuestionDialogFragment.show(getSupportFragmentManager(), "gotoQuestion");*/
            }

        }
    }

    @Override
    public void dismissDialog() {

    }

    @Override
    public void doAtPositiveButton(String tag) {
        switch (tag) {
            case "submitDialog":
                timer.cancel();
                submitPaper();
                break;
            case "quitDialog":
                timer.cancel();
                finish();
                break;
        }
    }

    @Override
    public void onDialogItemSelect(String tag, Bundle args) {

    }


    private void mShowDialog(int type, String msg) {
        switch (type) {
            case CONFIRM_QUIT:
                if (confirmQuitDialog == null) {
                    confirmQuitDialog = ConfirmAlertDialogFragment.newInstance(R.style.DialogStyle, getString(R.string.title_quit), getString(R.string.msg_quit_test));
                }
                confirmQuitDialog.show(getSupportFragmentManager(), "quitDialog");
                break;
            case CONFIRM_SUBMIT:
                if (confirmSubmitDialog == null) {
                    confirmSubmitDialog = ConfirmAlertDialogFragment.newInstance(R.style.DialogStyle, getString(R.string.title_submit), msg);
                }
                confirmSubmitDialog.show(getSupportFragmentManager(), "submitDialog");
                break;
            case PROGRESS:
                if (progressDialog == null) {
                    progressDialog = new ProgressDialog(TestActivity.this);
                    progressDialog.setCancelable(false);
                }
                progressDialog.setMessage(msg);
                progressDialog.show();
                break;
            case CHOOSE:
                if (gotoQuestionDialogFragment == null) {
                    gotoQuestionDialogFragment = GotoQuestionDialogFragment.newInstance(questionTested);
                }
                gotoQuestionDialogFragment.show(getSupportFragmentManager(), "gotoQuestion");
                break;
        }
    }

    private SQLiteDatabase getDataBase() {
        return db;
    }

    private String getPID() {
        return PID;
    }

    private String getSNO() {
        if (SNO == null) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            SNO = preferences.getString("ID", null);
        }
        return SNO;
    }

    private void updateTimeLastText() {
        if (count == 0) {
            // TODO: 2015/12/14 alert the student and submit the paper
            Toast.makeText(TestActivity.this, getString(R.string.toast_times_up), Toast.LENGTH_SHORT).show();
            if (timer != null) {
                timer.cancel();
            }
            submitPaper();
        }
        int h = count / 3600;
        int m = (count % 3600) / 60;
        int s = count % 60;
        String time = h + ":" + m + ":" + s;
        timeLast.setText(time);

    }

    private void startTimer() {
        if (timer == null) {
            timer = new Timer();
        }
        if (timerTask == null) {
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    if (handler != null) {
                        Message message = Message.obtain(handler, UPDATE_TIME_LAST);
                        handler.sendMessage(message);
                    }
                    count--;
                }
            };
        }
        timer.schedule(timerTask, 0, PERIOD);
    }

    private void submitPaper() {
      /*  progressDialog = new ProgressDialog(TestActivity.this);
        progressDialog.setCancelable(false);*/
        mShowDialog(PROGRESS, getResources().getString(R.string.msg_saving));
        db.delete("choiceAnswers", " SNO = ? and PID = ?", new String[]{getSNO(), getPID() + ""});
        db.delete("gapAnswers", " SNO = ? and PID = ?", new String[]{getSNO(), getPID() + ""});
        db.delete("essayAnswers", " SNO = ? and PID = ?", new String[]{getSNO(), getPID() + ""});
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < mAdapter.getCount(); i++) {
                    if (mAdapter.getFragment(i) != null && questionTested[i]) {
                        ((TestFragment) mAdapter.getFragment(i)).commitToDataBase();
                    }
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (handler != null) {
                    Message message = Message.obtain(handler, COMMIT_FINISHED);
                    handler.sendMessage(message);
                }
            }
        }).start();
        //progressDialog.dismiss();
    }

    private void initPaperView() {
        timeLast = (TextView) findViewById(R.id.time_last_text);
        TextView countText = (TextView) findViewById(R.id.count_text);
        String countHint = "/" + (choiceCount + gapCount + essayCount);
        countText.setText(countHint);
        questionTested = new boolean[choiceCount + gapCount + essayCount];
        final EditText gotoEdit = (EditText) findViewById(R.id.goto_edit);
        gotoEdit.setOnClickListener(this);
        pager = (ViewPager) findViewById(R.id.pager);
        mAdapter = new TestAdapter(getSupportFragmentManager());
        pager.setAdapter(mAdapter);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                String currentPage = position + 1 + "";
                gotoEdit.setText(currentPage);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    static private class mHandler extends Handler {
        private WeakReference<Activity> mActivity;

        public mHandler(Activity mActivity) {
            this.mActivity = new WeakReference<>(mActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            TestActivity activity = (TestActivity) mActivity.get();
            switch (msg.what) {
                case UPDATE_TIME_LAST:
                    // updateTimeLastText();
                    if (mActivity.get() instanceof TestActivity) {
                        activity.updateTimeLastText();
                    }
                    break;
                case COMMIT_FINISHED:
                    if (mActivity.get() instanceof TestActivity) {
                        activity.progressDialog.dismiss();
                        activity.finish();
                    }
                    break;
                case PAPER_PREPARED:
                    if (mActivity.get() instanceof TestActivity) {
                        activity.initPaperView();
                        activity.progressDialog.dismiss();
                        activity.startTimer();
                    }
                    break;
            }
        }
    }

    private class TestAdapter extends FragmentStatePagerAdapter {

        private Map<Integer, Fragment> map = new HashMap<>();

        public TestAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment f = null;
            if (0 <= position && position < choiceCount) {
                choiceCursor.moveToPosition(position);
                Bundle bundle = new Bundle();
                bundle.putInt("type", 0);
                bundle.putInt("position", position);
                bundle.putInt("QID", choiceCursor.getInt(choiceCursor.getColumnIndex("QID")));
                bundle.putInt("score", choiceCursor.getInt(choiceCursor.getColumnIndex("SCORE")));
                bundle.putString("answer", choiceCursor.getString(choiceCursor.getColumnIndex("ANSWER")));
                bundle.putString("content", choiceCursor.getString(choiceCursor.getColumnIndex("CONTENT")));
                bundle.putString("choiceA", choiceCursor.getString(choiceCursor.getColumnIndex("CHOICEA")));
                bundle.putString("choiceB", choiceCursor.getString(choiceCursor.getColumnIndex("CHOICEB")));
                bundle.putString("choiceC", choiceCursor.getString(choiceCursor.getColumnIndex("CHOICEC")));
                bundle.putString("choiceD", choiceCursor.getString(choiceCursor.getColumnIndex("CHOICED")));
                f = TestFragment.newInstance(bundle);
            } else if (choiceCount <= position && position < choiceCount + gapCount) {
                gapCursor.moveToPosition(position - choiceCount);
                Bundle bundle = new Bundle();
                bundle.putInt("type", 1);
                bundle.putInt("position", position);
                bundle.putInt("QID", gapCursor.getInt(gapCursor.getColumnIndex("QID")));
                bundle.putString("content", gapCursor.getString(gapCursor.getColumnIndex("CONTENT")));
                f = TestFragment.newInstance(bundle);
            } else if (position < choiceCount + gapCount + essayCount) {
                essayCursor.moveToPosition(position - choiceCount - gapCount);
                Bundle bundle = new Bundle();
                bundle.putInt("type", 2);
                bundle.putInt("position", position);
                bundle.putInt("QID", essayCursor.getInt(essayCursor.getColumnIndex("QID")));
                bundle.putString("content", essayCursor.getString(essayCursor.getColumnIndex("CONTENT")));
                f = TestFragment.newInstance(bundle);
            }
            map.put(position, f);
            return f;
        }

        @Override
        public int getCount() {
            return choiceCount + gapCount + essayCount;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
            //map.remove(position);
        }

        public Fragment getFragment(int position) {
            return map.get(position);
        }

        public int getMapSize() {
            return map.size();
        }

    }

    public static class TestFragment extends Fragment {

        int TYPE;
        int QID;
        int SCORE;
        int position;
        Context context;
        String choiceAnswer;
        RadioGroup radioGroup;
        TextView answerSheet;
        final int CHOICE = 0, GAP = 1, ESSAY = 2;

        static TestFragment newInstance(Bundle bundle) {
            TestFragment fragment = new TestFragment();
            fragment.setArguments(bundle);
            return fragment;
        }

        @Override
        public void onAttach(Context context) {
            super.onAttach(context);
            this.context = context;
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Bundle args = getArguments();
            if (args != null) {
                TYPE = args.getInt("type");
                QID = args.getInt("QID");
                position = args.getInt("position");
            } else {
                TYPE = -1;
                QID = -1;
            }
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = null;
            Bundle args = getArguments();
            switch (TYPE) {
                case CHOICE: {
                    choiceAnswer = args.getString("answer");
                    SCORE = args.getInt("score");
                    v = inflater.inflate(R.layout.fragment_test_choice, container, false);
                    TextView content = (TextView) v.findViewById(R.id.content);
                    radioGroup = (RadioGroup) v.findViewById(R.id.radio_group);
                    RadioButton buttonA = (RadioButton) v.findViewById(R.id.A);
                    RadioButton buttonB = (RadioButton) v.findViewById(R.id.B);
                    RadioButton buttonC = (RadioButton) v.findViewById(R.id.C);
                    RadioButton buttonD = (RadioButton) v.findViewById(R.id.D);
                    content.setText(args.getString("content"));
                    buttonA.setText(args.getString("choiceA"));
                    buttonB.setText(args.getString("choiceB"));
                    buttonC.setText(args.getString("choiceC"));
                    buttonD.setText(args.getString("choiceD"));
                    radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(RadioGroup group, int checkedId) {
                            if (getActivity() instanceof TestActivity) {
                                ((TestActivity) getActivity()).questionTested[position] = true;
                            }
                        }
                    });
                }
                break;
                case GAP: {
                    v = inflater.inflate(R.layout.fragment_test_gap, container, false);
                    TextView content = (TextView) v.findViewById(R.id.content);
                    answerSheet = (TextView) v.findViewById(R.id.answer_sheet);
                    answerSheet.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            if (getActivity() instanceof TestActivity) {
                                ((TestActivity) getActivity()).questionTested[position] = !s.toString().isEmpty();
                            }
                        }
                    });
                    content.setText(args.getString("content"));
                }
                break;
                case ESSAY: {
                    v = inflater.inflate(R.layout.fragment_test_essay, container, false);
                    answerSheet = (TextView) v.findViewById(R.id.answer_sheet);
                    answerSheet.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            if (getActivity() instanceof TestActivity) {
                                ((TestActivity) getActivity()).questionTested[position] = !s.toString().isEmpty();
                            }
                        }
                    });
                    TextView content = (TextView) v.findViewById(R.id.content);
                    content.setText(args.getString("content"));
                }
                break;
            }
            return v;
        }

        public void commitToDataBase() {
            // TODO: 2015/12/14 commit data to db
            SQLiteDatabase db = ((TestActivity) context).getDataBase();
            String PID = ((TestActivity) context).getPID();
            String SNO = ((TestActivity) context).getSNO();
            switch (TYPE) {
                case CHOICE: {
                    String selection;
                    switch (radioGroup.getCheckedRadioButtonId()) {
                        case R.id.A:
                            selection = "A";
                            break;
                        case R.id.B:
                            selection = "B";
                            break;
                        case R.id.C:
                            selection = "C";
                            break;
                        case R.id.D:
                            selection = "D";
                            break;
                        default:
                            selection = "#";
                    }
                    ContentValues vals = new ContentValues();
                    vals.put("ANSWER", selection);
                    vals.put("SCORE", selection.equals(choiceAnswer) ? SCORE : 0);
                    vals.put("SNO", SNO);
                    vals.put("QID", QID);
                    vals.put("PID", PID);
                    db.insert("choiceAnswers", null, vals);
                    break;
                }
                case GAP: {
                    ContentValues vals = new ContentValues();
                    vals.put("ANSWER", answerSheet.getText().toString());
                    //vals.put("SCORE", 0);
                    vals.put("SNO", SNO);
                    vals.put("QID", QID);
                    vals.put("PID", PID);
                    db.insert("gapAnswers", null, vals);
                    break;
                }
                case ESSAY: {
                    ContentValues vals = new ContentValues();
                    vals.put("ANSWER", answerSheet.getText().toString());
                    //vals.put("SCORE", 0);
                    vals.put("SNO", SNO);
                    vals.put("QID", QID);
                    vals.put("PID", PID);
                    db.insert("essayAnswers", null, vals);
                    break;
                }
                default:
                    Log.e("error", "typeerror");
            }
        }

    }
}
