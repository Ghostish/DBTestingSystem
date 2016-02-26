package com.bbt.kangel.dbtesingsystem.activity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bbt.kangel.dbtesingsystem.R;
import com.bbt.kangel.dbtesingsystem.util.DataBaseHelper;
import com.bbt.kangel.dbtesingsystem.util.GlobalKeeper;
import com.bbt.kangel.dbtesingsystem.util.TestDataBaseUtil;

/**
 * Created by Kangel on 2016/2/23.
 */
public class DeanViewPeopleDetailActivity extends AppCompatActivity implements View.OnClickListener {
    private String ID;
    private int TYPE;
    private boolean isInfoUpdated = false;
    private TextView nameText, idText, majorText, passWordText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getIntent().getExtras();
        try {
            ID = args.getString("ID");
            TYPE = args.getInt("TYPE");
        } catch (Exception e) {
            throw new NullPointerException(this.toString() + " must call this activity with valid params set in bundle");
        }
        setContentView(R.layout.activity_dean_view_people_detail);
        initView();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            isInfoUpdated = true;
            Bundle args = data.getExtras();
            nameText.setText(args.getString("NAME"));
            if (majorText != null) {
                majorText.setText(args.getString("MAJOR"));
            }
        }
    }


    @Override
    public void onBackPressed() {
        if (isInfoUpdated) {
            setResult(RESULT_OK);
        }else {
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab: {
                Intent intent = new Intent(DeanViewPeopleDetailActivity.this, DeanAddOrEditAccountActivity.class);
                Bundle args = new Bundle();
                args.putInt("MODE", DeanAddOrEditAccountActivity.MODE_EDIT);
                args.putInt("TYPE", TYPE);
                args.putString("NAME", nameText.getText().toString());
                args.putString("ID", idText.getText().toString());
                if (majorText != null) {
                    args.putString("MAJOR", majorText.getText().toString());
                }
                intent.putExtras(args);
                startActivityForResult(intent, TYPE);
            }
            default: {
                if (isInfoUpdated) {
                    setResult(RESULT_OK);
                }
                finish();

            }
        }
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(this);
        SQLiteDatabase db = new DataBaseHelper(DeanViewPeopleDetailActivity.this, GlobalKeeper.DB_NAME, 1).getReadableDatabase();
        nameText = (TextView) findViewById(R.id.name_text);
        idText = (TextView) findViewById(R.id.id_text);
        passWordText = (TextView) findViewById(R.id.password_text);
        ImageView iconImage = (ImageView) findViewById(R.id.head_show_image);
        TextView idLabel = (TextView) findViewById(R.id.id_label);
        CardView majorCard = (CardView) findViewById(R.id.major_info_card);
        switch (TYPE) {
            case GlobalKeeper.TYPE_STUDENT: {
                majorText = (TextView) findViewById(R.id.major_text);
                iconImage.setImageResource(R.mipmap.student_head_show);
                idLabel.setText(R.string.label_sno_without_colon);
                majorCard.setVisibility(View.VISIBLE);
                Cursor c = TestDataBaseUtil.getSinglePeopleByID(db, GlobalKeeper.TYPE_STUDENT, ID);
                if (c != null && c.moveToNext()) {
                    nameText.setText(c.getString(c.getColumnIndex("SNAME")));
                    idText.setText(c.getString(c.getColumnIndex("SNO")));
                    majorText.setText(c.getString(c.getColumnIndex("MAJOR")));
                    c.close();
                }
                break;
            }
            case GlobalKeeper.TYPE_TEACHER: {
                iconImage.setImageResource(R.mipmap.teacher_head_show);
                idLabel.setText(R.string.label_tno_without_colon);
                Cursor c = TestDataBaseUtil.getSinglePeopleByID(db, GlobalKeeper.TYPE_TEACHER, ID);
                if (c != null && c.moveToNext()) {
                    nameText.setText(c.getString(c.getColumnIndex("TNAME")));
                    idText.setText(c.getString(c.getColumnIndex("TNO")));
                    c.close();
                }
                break;
            }
        }
        db.close();
    }
}
