package com.bbt.kangel.dbtesingsystem.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.bbt.kangel.dbtesingsystem.R;
import com.bbt.kangel.dbtesingsystem.fragment.ConfirmAlertDialogFragment;
import com.bbt.kangel.dbtesingsystem.fragment.SelectPaperDialogFragment;
import com.bbt.kangel.dbtesingsystem.util.DialogActivity;

/**
 * Created by Kangel on 2015/12/14.
 */
public class TeacherMainActivity extends AppCompatActivity implements View.OnClickListener, DialogActivity {
    private SelectPaperDialogFragment selectPaperDialogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_main);
        TextView nameText = (TextView) findViewById(R.id.name);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String name = preferences.getString("name",null);
        if(name != null){
            nameText.setText(name);
        }
    }

    @Override
    public void dismissDialog() {

    }

    @Override
    public void doAtPositiveButton(String tag) {
        switch (tag) {
            case "logoutDialog":
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                preferences.edit().putInt("LOGIN_TYPE", -1).apply();
                Log.d("preferences", preferences.getInt("LOGIN_TYPE", -1) + "");
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }

    @Override
    public void onItemSelected(Bundle args) {
        Intent intent = new Intent(TeacherMainActivity.this, TeacherChooseUnmarkedActivity.class);
        intent.putExtras(args);
        startActivity(intent);
        selectPaperDialogFragment.dismiss();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mark_papers: {
                if (selectPaperDialogFragment == null) {
                    selectPaperDialogFragment = new SelectPaperDialogFragment();
                }
                selectPaperDialogFragment.show(getSupportFragmentManager(), "select");
                break;
            }
            case R.id.logout_button: {
                ConfirmAlertDialogFragment f = ConfirmAlertDialogFragment.newInstance(R.style.DialogStyle, getString(R.string.title_logout), getString(R.string.msg_logout));
                f.show(getSupportFragmentManager(), "logoutDialog");
                break;
            }
        }
    }
}
