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
import com.bbt.kangel.dbtesingsystem.util.DialogActivity;

/**
 * Created by Kangel on 2015/12/14.
 */
public class DeanMainActivity extends AppCompatActivity implements View.OnClickListener, DialogActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dean_main);
        TextView nameText = (TextView) findViewById(R.id.name);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String name = preferences.getString("name", null);
        if (name != null) {
            nameText.setText(getString(R.string.dean_welcome, name));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.account_management_button: {
                Intent intent = new Intent(DeanMainActivity.this, DeanManageActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.logout_button: {
                ConfirmAlertDialogFragment f = ConfirmAlertDialogFragment.newInstance(R.style.DialogStyle, getString(R.string.title_logout), getString(R.string.msg_logout));
                f.show(getSupportFragmentManager(), "logoutDialog");
            }
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
    public void onDialogItemSelect(Bundle args) {

    }
}
