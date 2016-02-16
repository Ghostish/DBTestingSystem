package com.bbt.kangel.dbtesingsystem.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.bbt.kangel.dbtesingsystem.R;
import com.bbt.kangel.dbtesingsystem.util.DialogActivity;

import com.bbt.kangel.dbtesingsystem.fragment.ConfirmAlertDialogFragment;
import com.bbt.kangel.dbtesingsystem.fragment.SelectPaperDialogFragment;

public class StudentMainActivity extends AppCompatActivity implements View.OnClickListener,DialogActivity{

    private SelectPaperDialogFragment dialogFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_main);
        TextView nameText = (TextView) findViewById(R.id.name);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String name = preferences.getString("name",null);
       if(name != null){
           nameText.setText(getString(R.string.student_welcome,name));
       }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.start_test:
                // TODO: 2015/12/14 show a dialog to pick test
                if (dialogFragment == null) {
                    dialogFragment = new SelectPaperDialogFragment();
                    dialogFragment.show(getSupportFragmentManager(), "selectDialog");
                    Log.d("dialog", "firstshow");
                }else {
                    dialogFragment.show(getSupportFragmentManager(), "selectDialog");
                    Log.d("dialog","reshow");
                }
                break;
            case R.id.show_result:
                // TODO: 2015/12/14 start an activity to show result
                Intent intent = new Intent(this,StudentViewGradeActivity.class);
                startActivity(intent);
                break;
            case R.id.logout_button:
                ConfirmAlertDialogFragment f = ConfirmAlertDialogFragment.newInstance(R.style.DialogStyle,getString(R.string.title_logout),getString(R.string.msg_logout));
                f.show(getSupportFragmentManager(),"logoutDialog");
        }
    }

    @Override
    public void dismissDialog() {
        if(dialogFragment != null){
            dialogFragment.dismiss();
        }
    }

    @Override
    public void doAtPositiveButton(String tag) {
        switch (tag){
            case "logoutDialog":
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                preferences.edit().putInt("LOGIN_TYPE",-1).apply();
                Log.d("preferences", preferences.getInt("LOGIN_TYPE", -1) + "");
                Intent intent = new Intent(this,LoginActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }

    @Override
    public void onDialogItemSelect(Bundle args) {
        Intent intent = new Intent(StudentMainActivity.this, TestActivity.class);
        intent.putExtras(args);
        startActivity(intent);
        dismissDialog();
    }
}
