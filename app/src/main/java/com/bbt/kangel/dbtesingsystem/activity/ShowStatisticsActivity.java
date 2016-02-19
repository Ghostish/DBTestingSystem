package com.bbt.kangel.dbtesingsystem.activity;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bbt.kangel.dbtesingsystem.R;

/**
 * Created by Kangel on 2016/2/19.
 */
public class ShowStatisticsActivity extends AppCompatActivity {
    private ProgressBar maxGradeProgress, minGradeProgress, avgGradeProgress;
    private TextView maxGradeText, minGradeText, avgGradeText;
    private int maxGrade,minGrade, avgGrade;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        overridePendingTransition(R.anim.anim_slide_down, R.anim.anim_slide_up);

        maxGradeProgress = (ProgressBar) findViewById(R.id.progress_max_grade);
        minGradeProgress = (ProgressBar) findViewById(R.id.progress_min_grade);
        avgGradeProgress = (ProgressBar) findViewById(R.id.progress_average_grade);
        maxGradeText = (TextView) findViewById(R.id.text_max_grade);
        minGradeText = (TextView) findViewById(R.id.text_min_grade);
        avgGradeText = (TextView) findViewById(R.id.text_average_grade);

        Bundle args = getIntent().getExtras();
        if (args != null) {
            maxGrade = args.getInt("max");
            minGrade = args.getInt("min");
            avgGrade = args.getInt("avg");
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.anim_slide_down, R.anim.anim_slide_up);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus)
        animateGradeStatistics(maxGradeProgress, maxGradeText, maxGrade);
        animateGradeStatistics(minGradeProgress,minGradeText,minGrade);
        animateGradeStatistics(avgGradeProgress, avgGradeText, avgGrade);
    }

    private void animateGradeStatistics(final ProgressBar progressBar, final TextView textView, int grade) {
        ValueAnimator animator = ValueAnimator.ofInt(0,grade);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int currentValues = (int) animation.getAnimatedValue();
                progressBar.setProgress(currentValues);
                textView.setText(currentValues + "分");
            }
        });
        animator.setDuration(1000);
        animator.start();
    }
}
