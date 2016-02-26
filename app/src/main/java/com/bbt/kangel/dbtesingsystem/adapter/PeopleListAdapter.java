package com.bbt.kangel.dbtesingsystem.adapter;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bbt.kangel.dbtesingsystem.R;
import com.bbt.kangel.dbtesingsystem.util.GlobalKeeper;
import com.bbt.kangel.dbtesingsystem.util.ItemTouchHelperActivity;
import com.bbt.kangel.dbtesingsystem.util.ItemTouchHelperAdapter;
import com.bbt.kangel.dbtesingsystem.util.RecyclerViewActivity;

/**
 * Created by Kangel on 2016/2/18.
 */
public class PeopleListAdapter extends RecyclerView.Adapter<PeopleListAdapter.ViewHolder> {
    private Context context;
    private Cursor cursor;

    public PeopleListAdapter(Context context, Cursor cursor) {
        this.context = context;
        this.cursor = cursor;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        switch (viewType) {
            case GlobalKeeper.TYPE_STUDENT: {
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_student_info, parent, false);
                v.setTag("students");
                return new ViewHolder(v);
            }
            case GlobalKeeper.TYPE_TEACHER: {
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_teacher_info, parent, false);
                v.setTag("teachers");
                return new ViewHolder(v);
            }
            default: {
                Log.e("typeError", "no such viewType");
                return null;
            }
        }
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        cursor.moveToPosition(position);
        holder.nameText.setText(cursor.getString(cursor.getColumnIndex("NAME")));
        holder.idText.setText(cursor.getString(cursor.getColumnIndex("ID")));
        if (!holder.itemView.hasOnClickListeners()) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (context instanceof RecyclerViewActivity) {
                        Bundle bundle = new Bundle();
                        bundle.putString("ID", holder.idText.getText().toString());
                        bundle.putInt("TYPE", getItemViewType(holder.getAdapterPosition()));
                        ((RecyclerViewActivity) context).onRecyclerViewItemSelect(bundle, ((String) holder.itemView.getTag()),holder.getAdapterPosition());
                    }
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        cursor.moveToPosition(position);
        return cursor.getInt(cursor.getColumnIndex("TYPE"));
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

   /* @Override
    public void onItemMove(int fromPosition, int toPosition) {

    }

    @Override
    public void onItemDismiss(int position) {
        // TODO: 2016/2/21 move this to activity
        cursor.moveToPosition(position);
        String id = cursor.getString(cursor.getColumnIndex("ID"));
        //int type = cursor.getInt(cursor.getColumnIndex("TYPE"));
        Bundle args = new Bundle();
        args.putString("id", id);
        args.putInt("position",position);
        if (context instanceof ItemTouchHelperActivity) {
            ((ItemTouchHelperActivity) context).onItemSwiped(args);
        }
    }*/

    public void updateCursor(Cursor cursor) {
        this.cursor = cursor;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView idText;
        private TextView nameText;

        public ViewHolder(View itemView) {
            super(itemView);
            idText = (TextView) itemView.findViewById(R.id.id_text);
            nameText = (TextView) itemView.findViewById(R.id.name_text);
        }
    }
}
