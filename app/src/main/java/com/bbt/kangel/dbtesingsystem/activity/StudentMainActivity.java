package com.bbt.kangel.dbtesingsystem.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.bbt.kangel.dbtesingsystem.R;
import com.bbt.kangel.dbtesingsystem.util.DialogActivity;
import com.bbt.kangel.dbtesingsystem.util.mDataBaseHelper;

import fragment.ConfirmAlertDialogFragment;
import fragment.SelectPaperDialogFragment;

public class StudentMainActivity extends AppCompatActivity implements View.OnClickListener,DialogActivity{

    private SelectPaperDialogFragment dialogFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_main);
        TextView nameText = (TextView) findViewById(R.id.name);
        SharedPreferences preferences = getSharedPreferences("preferences",MODE_PRIVATE);
        String name = preferences.getString("name",null);
       if(name != null){
           nameText.setText(name);
       }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.start_test:
                // TODO: 2015/12/14 show a dialog to pick test
                dialogFragment = new SelectPaperDialogFragment();
                dialogFragment.show(getSupportFragmentManager(), "selectDialog");
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
                SharedPreferences preferences = getSharedPreferences("preferences",MODE_PRIVATE);
                preferences.edit().putInt("LOGIN_TYPE",-1).apply();
                Intent intent = new Intent(this,LoginActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }
}
