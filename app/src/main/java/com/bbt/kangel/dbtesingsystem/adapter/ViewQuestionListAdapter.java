package com.bbt.kangel.dbtesingsystem.adapter;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import com.bbt.kangel.dbtesingsystem.R;
import com.bbt.kangel.dbtesingsystem.util.GlobalKeeper;
import com.bbt.kangel.dbtesingsystem.util.RecyclerViewActivity;

import java.util.ArrayList;

/**
 * Created by Kangel on 2016/2/20.
 */
public class ViewQuestionListAdapter extends RecyclerView.Adapter<ViewQuestionListAdapter.ViewHolder> {

    private Context context;
    private Cursor cursor;
    private boolean itemClickable;
    private boolean showScore;
    private String contentFormatString;
    private String tag;

    private int selectedBackGround, unselectedBackGround;
    private boolean[] selectedArray;

    public ViewQuestionListAdapter(Context context, Cursor cursor, boolean itemClickable, boolean showScore) {
        this.context = context;
        this.cursor = cursor;
        this.itemClickable = itemClickable;
        this.showScore = showScore;
        if (showScore) {
            contentFormatString = context.getString(R.string.question_with_score);
        } else {
            contentFormatString = context.getString(R.string.question_without_score);
        }
        if (itemClickable) {
            selectedBackGround = ContextCompat.getColor(context, R.color.lightGrey);
            unselectedBackGround = ContextCompat.getColor(context, R.color.transparent);
            selectedArray = new boolean[cursor.getCount()];
        }
    }
    public ViewQuestionListAdapter(Context context, Cursor cursor, boolean itemClickable, boolean showScore,String tag) {
        this(context, cursor, itemClickable, showScore);
        this.tag = tag;
    }

    @Override
    public int getItemViewType(int position) {
        cursor.moveToPosition(position);
        return cursor.getInt(cursor.getColumnIndex("TYPE"));
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        switch (viewType) {
            case GlobalKeeper.TYPE_CHOICE: {
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_choice_question, parent, false);
                v.setTag(GlobalKeeper.TYPE_CHOICE);
                break;
            }
            default: {
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_gap_essay_question, parent, false);
                v.setTag(GlobalKeeper.TYPE_ESSAY | GlobalKeeper.TYPE_GAP);
            }
        }
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        cursor.moveToPosition(position);
        int type = cursor.getInt(cursor.getColumnIndex("TYPE"));
        if (showScore) {
            holder.contentText.setText(String.format(contentFormatString, position + 1, cursor.getString(cursor.getColumnIndex("CONTENT")), cursor.getInt(cursor.getColumnIndex("SCORE"))));
        } else {
            holder.contentText.setText(String.format(contentFormatString, position + 1, cursor.getString(cursor.getColumnIndex("CONTENT"))));
        }
        if (type == GlobalKeeper.TYPE_CHOICE) {
            holder.buttonA.setText(cursor.getString(cursor.getColumnIndex("CHOICEA")));
            holder.buttonB.setText(cursor.getString(cursor.getColumnIndex("CHOICEB")));
            holder.buttonC.setText(cursor.getString(cursor.getColumnIndex("CHOICEC")));
            holder.buttonD.setText(cursor.getString(cursor.getColumnIndex("CHOICED")));
            switch (cursor.getString(cursor.getColumnIndex("ANSWER"))) {
                case "A":
                    holder.buttonA.setChecked(true);
                    break;
                case "B":
                    holder.buttonB.setChecked(true);
                    break;
                case "C":
                    holder.buttonC.setChecked(true);
                    break;
                case "D":
                    holder.buttonD.setChecked(true);
                    break;
            }
        }
        if (type == GlobalKeeper.TYPE_GAP || type == GlobalKeeper.TYPE_ESSAY) {
            holder.AnswerText.setText(cursor.getString(cursor.getColumnIndex("ANSWER")));
        }
        if (itemClickable) {
            setBackGround(selectedArray[holder.getAdapterPosition()], holder.itemView);
        }
        if (!holder.itemView.hasOnClickListeners() && itemClickable) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedArray[holder.getAdapterPosition()] = !selectedArray[holder.getAdapterPosition()];
                    setBackGround(selectedArray[holder.getAdapterPosition()], holder.itemView);
                    // TODO: 2016/2/20 call activity method
                    if (context instanceof RecyclerViewActivity) {
                        Bundle args = new Bundle();
                        args.putInt("position", holder.getAdapterPosition());
                        args.putBoolean("isPicked", selectedArray[holder.getAdapterPosition()]);
                        ((RecyclerViewActivity) context).onRecyclerViewItemSelect(args,tag);
                    }
                }
            });
        }

    }

    void setBackGround(boolean selected, View itemView) {
        if (selected) {
            itemView.setBackgroundColor(selectedBackGround);
        } else {
            itemView.setBackgroundColor(unselectedBackGround);
        }
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView contentText;
        TextView AnswerText;

        RadioButton buttonA, buttonB, buttonC, buttonD;

        public ViewHolder(View itemView) {
            super(itemView);
            contentText = (TextView) itemView.findViewById(R.id.content);

            if (((int) itemView.getTag()) == GlobalKeeper.TYPE_CHOICE) {
                buttonA = (RadioButton) itemView.findViewById(R.id.A);
                buttonB = (RadioButton) itemView.findViewById(R.id.B);
                buttonC = (RadioButton) itemView.findViewById(R.id.C);
                buttonD = (RadioButton) itemView.findViewById(R.id.D);
            } else {
                AnswerText = (TextView) itemView.findViewById(R.id.edit_reference_answers);

            }
        }
    }

    public void updateCursor(Cursor cursor) {
        this.cursor = cursor;
    }
}
