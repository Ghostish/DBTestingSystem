package com.bbt.kangel.dbtesingsystem.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bbt.kangel.dbtesingsystem.R;
import com.bbt.kangel.dbtesingsystem.activity.TestActivity;
import com.bbt.kangel.dbtesingsystem.util.DialogActivity;
import com.bbt.kangel.dbtesingsystem.util.RecyclerViewActivity;

/**
 * Created by Kangel on 2015/12/14.
 */
public class ViewPaperListAdapter extends RecyclerView.Adapter<ViewPaperListAdapter.ViewHolder> {
    private Context context;
    private Cursor cursor;

    public ViewPaperListAdapter(Context context, Cursor cursor) {
        this.context = context;
        this.cursor = cursor;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_paper_info,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        cursor.moveToPosition(position);
        holder.pidText.setText(String.valueOf(cursor.getInt(cursor.getColumnIndex("PID"))));
        holder.createTimeText.setText(cursor.getString(cursor.getColumnIndex("CREATETIME")));
        if(!holder.itemView.hasOnClickListeners()){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String pid = holder.pidText.getText().toString();
                    Bundle bundle = new Bundle();
                    bundle.putString("PID", pid);
                    /*context.startActivity(intent);
                    ((DialogActivity) context).dismissDialog();*/
                    if(context instanceof DialogActivity){
                        // TODO: 2016/2/21 use a tag variable to save the selectPaper tag
                        ((DialogActivity) context).onDialogItemSelect("selectPaper", bundle);
                    }else if (context instanceof RecyclerViewActivity) {
                        ((RecyclerViewActivity) context).onRecyclerViewItemSelect(bundle,"paperList");
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

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView pidText,createTimeText;

        public ViewHolder(View itemView) {
            super(itemView);
            pidText = (TextView) itemView.findViewById(R.id.PID_text);
            createTimeText = (TextView) itemView.findViewById(R.id.create_time_text);
        }
    }
}
