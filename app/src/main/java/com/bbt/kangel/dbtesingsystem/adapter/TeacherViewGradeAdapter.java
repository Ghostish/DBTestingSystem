package com.bbt.kangel.dbtesingsystem.adapter;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bbt.kangel.dbtesingsystem.R;
import com.bbt.kangel.dbtesingsystem.util.RecyclerViewActivity;

/**
 * Created by Kangel on 2015/12/13.
 */
public class TeacherViewGradeAdapter extends RecyclerView.Adapter<TeacherViewGradeAdapter.ViewHolder> {

    private Context context;
    private Cursor cursor;

    public TeacherViewGradeAdapter(Context context, Cursor cursor) {
        this.context = context;
        this.cursor = cursor;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView text_sno, text_name, text_pid, text_grade;

        public ViewHolder(View itemView) {
            super(itemView);
            text_sno = (TextView) itemView.findViewById(R.id.sno);
            text_name = (TextView) itemView.findViewById(R.id.sname);
            text_pid = (TextView) itemView.findViewById(R.id.PID);
            text_grade = (TextView) itemView.findViewById(R.id.grade);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_grade_list_teacher, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        cursor.moveToPosition(position);
        holder.text_sno.setText(cursor.getString(cursor.getColumnIndex("SNO")));
        holder.text_name.setText(cursor.getString(cursor.getColumnIndex("SNAME")));
        String GRADE = cursor.getString(cursor.getColumnIndex("GRADE"));
        holder.text_grade.setText(GRADE);
        String PAPER_ID = cursor.getString(cursor.getColumnIndex("PID"));
        holder.text_pid.setText(PAPER_ID);
        if (!holder.itemView.hasOnClickListeners()) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(context instanceof RecyclerViewActivity){
                        Bundle args = new Bundle();
                        args.putString("sno", cursor.getString(cursor.getColumnIndex("SNO")));
                        args.putInt("pid",cursor.getInt(cursor.getColumnIndex("PID")));
                        ((RecyclerViewActivity) context).onRecyclerViewItemSelect(args);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

    public void updateCursor(Cursor c) {
        this.cursor = c;
    }
}
