package com.bbt.kangel.dbtesingsystem.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bbt.kangel.dbtesingsystem.R;
import com.bbt.kangel.dbtesingsystem.util.GlobalKeeper;
import com.bbt.kangel.dbtesingsystem.util.DataBaseHelper;

/**
 * Created by Kangel on 2015/12/5.
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher {
    private EditText editUsername, editPassword;
    private Spinner spinner;
    private TextView loginTextButton;
    private SQLiteDatabase db;
    private DataBaseHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Log.d("preferences", preferences.getInt("LOGIN_TYPE", -1) + "");
        if (preferences.getInt("LOGIN_TYPE", -1) != -1) {
            Intent intent = new Intent();
            switch (preferences.getInt("LOGIN_TYPE", -1)) {
                case GlobalKeeper.TYPE_STUDENT:
                    intent.setClass(this, StudentMainActivity.class);
                    break;
                case GlobalKeeper.TYPE_TEACHER:
                    intent.setClass(this, TeacherMainActivity.class);
                    break;
                case GlobalKeeper.TYPE_DEAN:
                    intent.setClass(this, DeanMainActivity.class);
                    break;
            }
            startActivity(intent);
            finish();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_layout);
        spinner = (Spinner) findViewById(R.id.spinner);
        editUsername = (EditText) findViewById(R.id.edit_username);
        editPassword = (EditText) findViewById(R.id.edit_password);
        editUsername.addTextChangedListener(this);
        editPassword.addTextChangedListener(this);
        loginTextButton = (TextView) findViewById(R.id.login_button_text);
        loginTextButton.setClickable(false);
        String[] roleArray = getResources().getStringArray(R.array.role_array);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, roleArray);
        spinner.setAdapter(adapter);
        helper = new DataBaseHelper(this, GlobalKeeper.DB_NAME, 1);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_button_text:
                // TODO: 2015/12/5 do something
                String[] selectionArgs = new String[]{editUsername.getText().toString(), editPassword.getText().toString()};
                db = helper.getReadableDatabase();
                Cursor c;
                switch (spinner.getSelectedItemPosition()) {
                    case 0://students
                        c = db.rawQuery("select * from students where SNO = ? and PASSWORD = ?", selectionArgs);
                        if (c.getCount() == 1) {
                            /*write to sharePreferences to save user's info ,including user's type and user's ID(SNO,TNO,or DNO)*/
                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putInt("LOGIN_TYPE", 0);
                            editor.putString("ID", selectionArgs[0]);
                            c.moveToPosition(0);
                            editor.putString("name", c.getString(c.getColumnIndex("SNAME")));
                            editor.apply();
                            Intent intent = new Intent(this, StudentMainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(this, getString(R.string.error_login), Toast.LENGTH_SHORT).show();
                        }
                        c.close();
                        break;
                    case 1://teachers
                        c = db.rawQuery("select * from teachers where TNO = ? and PASSWORD = ?", selectionArgs);
                        if (c.getCount() == 1) {
                            /*write to sharePreferences to save user's info ,including user's type and user's ID(SNO,TNO,or DNO)*/
                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putInt("LOGIN_TYPE", 1);
                            editor.putString("ID", selectionArgs[0]);
                            c.moveToPosition(0);
                            editor.putString("name", c.getString(c.getColumnIndex("TNAME")));
                            editor.apply();
                            Intent intent = new Intent(this, TeacherMainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(this, getString(R.string.error_login), Toast.LENGTH_SHORT).show();
                        }
                        c.close();
                        break;
                    case 2://deans
                        c = db.rawQuery("select * from teachers where DNO = ? and PASSWORD = ?", selectionArgs);
                        if (c.getCount() == 1) {
                            /*write to sharePreferences to save user's info ,including user's type and user's ID(SNO,TNO,or DNO)*/
                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putInt("LOGIN_TYPE", 2);
                            editor.putString("ID", selectionArgs[0]);
                            c.moveToPosition(0);
                            editor.putString("name", c.getString(c.getColumnIndex("DNAME")));
                            editor.apply();
                            Intent intent = new Intent(this, DeanMainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(this, getString(R.string.error_login), Toast.LENGTH_SHORT).show();
                        }
                        c.close();
                }
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
        String username = editUsername.getText().toString();
        String password = editPassword.getText().toString();
        if (!username.isEmpty() && !password.isEmpty()) {
            loginTextButton.setClickable(true);
            loginTextButton.setAlpha(1.0f);
        } else {
            loginTextButton.setClickable(false);
            loginTextButton.setAlpha(0.54f);
        }
    }
}
