package com.bbt.kangel.dbtesingsystem.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bbt.kangel.dbtesingsystem.R;

/**
 * Created by Kangel on 2016/2/6.
 */
public class QuestionNumListAdapter extends RecyclerView.Adapter<QuestionNumListAdapter.ViewHolder> {
    private Context context;
    private boolean[] data;
    private int textAnswered, textNotAnswered, backGroundNotAnswered, backGroundAnswered;

    public QuestionNumListAdapter(Context context, boolean[] data) {
        this.context = context;
        this.data = data;
        textAnswered = ContextCompat.getColor(context, android.R.color.primary_text_dark);
        textNotAnswered = ContextCompat.getColor(context, android.R.color.primary_text_light);
        backGroundAnswered = ContextCompat.getColor(context, R.color.colorAccent);
        backGroundNotAnswered = ContextCompat.getColor(context, R.color.lightGrey);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_number_block, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.numText.setText(position + 1 + "");
        holder.ll.setBackgroundColor(data[position] ? backGroundAnswered : backGroundNotAnswered);
        holder.numText.setTextColor(data[position] ? textAnswered : textNotAnswered);
        if(!holder.numText.hasOnClickListeners()&&context instanceof View.OnClickListener){
            holder.numText.setOnClickListener((View.OnClickListener) context);
        }
    }

    @Override
    public int getItemCount() {
        return data.length;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout ll;
        TextView numText;

        public ViewHolder(View itemView) {
            super(itemView);
            ll = (LinearLayout) itemView.findViewById(R.id.num_holder);
            numText = (TextView) itemView.findViewById(R.id.question_num);
        }
    }
}
