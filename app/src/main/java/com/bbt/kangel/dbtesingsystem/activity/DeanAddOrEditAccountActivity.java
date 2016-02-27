package com.bbt.kangel.dbtesingsystem.activity;

import android.animation.ObjectAnimator;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.Toast;

import com.bbt.kangel.dbtesingsystem.R;
import com.bbt.kangel.dbtesingsystem.util.DataBaseHelper;
import com.bbt.kangel.dbtesingsystem.util.GlobalKeeper;

/**
 * Created by Kangel on 2016/2/24.
 */
public class DeanAddOrEditAccountActivity extends AppCompatActivity implements View.OnClickListener {
    public static final int MODE_ADD = 11, MODE_EDIT = 22;
    private int mode;
    private int type;
    private EditText nameEdit, idEdit, majorEdit, passWordEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getIntent().getExtras();
        mode = args.getInt("MODE");
        type = args.getInt("TYPE");
        if (!(mode == MODE_ADD || mode == MODE_EDIT) || !(type == GlobalKeeper.TYPE_STUDENT || type == GlobalKeeper.TYPE_TEACHER)) {
            throw new IllegalArgumentException(this.toString() + " must call this activity with valid type and mode set");
        }

        initView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ok_button: {
                boolean isAllFilled = true;
                if (nameEdit.getText().toString().isEmpty()) {
                    nameEdit.setError(getString(R.string.error_can_not_be_empty));
                    isAllFilled = false;
                }
                if (idEdit.getText().toString().isEmpty()) {
                    idEdit.setError(getString(R.string.error_can_not_be_empty));
                    isAllFilled = false;
                }
                if (passWordEdit.getText().toString().isEmpty()) {
                    passWordEdit.setError(getString(R.string.error_can_not_be_empty));
                    isAllFilled = false;
                }
                if (majorEdit != null && majorEdit.getText().toString().isEmpty()) {
                    majorEdit.setError(getString(R.string.error_can_not_be_empty));
                    isAllFilled = false;
                }
                if (isAllFilled) {
                    commitToDataBase();
                    finish();
                }
                break;
            }
            default: {
                finish();
            }
        }
    }

    private void commitToDataBase() {
        SQLiteDatabase db = new DataBaseHelper(DeanAddOrEditAccountActivity.this, GlobalKeeper.DB_NAME, 1).getWritableDatabase();
        ContentValues values = new ContentValues();
        String tableName;
        if (type == GlobalKeeper.TYPE_STUDENT) {
            /*for student*/
            values.put("SNO", idEdit.getText().toString());
            values.put("SNAME", nameEdit.getText().toString());
            values.put("MAJOR", majorEdit.getText().toString());
            values.put("PASSWORD", passWordEdit.getText().toString());
            tableName = "students";
        } else {
            /*for teacher*/
            values.put("TNO", idEdit.getText().toString());
            values.put("TNAME", nameEdit.getText().toString());
            values.put("PASSWORD", passWordEdit.getText().toString());
            tableName = "teachers";
        }
        if (mode == MODE_ADD) {
            try {
                db.insert(tableName, null, values);
                Toast.makeText(getApplicationContext(), R.string.add_account_successfully, Toast.LENGTH_LONG).show();
                setResult(RESULT_OK);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), R.string.add_account_failed, Toast.LENGTH_LONG).show();

            }
        } else {
            try {
                db.update(tableName, values, (type == GlobalKeeper.TYPE_STUDENT ? "SNO" : "TNO") + " = " + idEdit.getText().toString(), null);
                Toast.makeText(getApplicationContext(), R.string.edit_account_successfully, Toast.LENGTH_LONG).show();
                Intent intent = new Intent();
                Bundle args = new Bundle();
                args.putString("NAME", nameEdit.getText().toString());
                if (majorEdit != null) {
                    args.putString("MAJOR", majorEdit.getText().toString());
                }
                intent.putExtras(args);
                setResult(RESULT_OK, intent);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), R.string.edit_account_failed, Toast.LENGTH_LONG).show();
            }
        }
    }


    private void initView() {
        setContentView(R.layout.activity_dean_add_people_info);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(this);

        if (type == GlobalKeeper.TYPE_STUDENT) {
            findViewById(R.id.major_info_card).setVisibility(View.VISIBLE);
            majorEdit = (EditText) findViewById(R.id.major_edit);
        }

        nameEdit = (EditText) findViewById(R.id.name_edit);
        idEdit = (EditText) findViewById(R.id.id_edit);
        passWordEdit = (EditText) findViewById(R.id.password_edit);

        if (mode == MODE_EDIT) {
            idEdit.setEnabled(false);
            Bundle bundle = getIntent().getExtras();
            nameEdit.setText(bundle.getString("NAME"));
            idEdit.setText(bundle.getString("ID"));
            if (majorEdit != null) {
                majorEdit.setText(bundle.getString("MAJOR"));
            }
            passWordEdit.setText("hello123456"); //fake password

        }

    }
}
