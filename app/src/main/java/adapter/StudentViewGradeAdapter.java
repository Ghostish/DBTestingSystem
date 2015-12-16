package adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bbt.kangel.dbtesingsystem.R;

/**
 * Created by Kangel on 2015/12/14.
 */
public class StudentViewGradeAdapter extends RecyclerView.Adapter<StudentViewGradeAdapter.ViewHolder> {
    private Context context;
    private Cursor c;

    public StudentViewGradeAdapter(Context context, Cursor c) {
        this.context = context;
        this.c = c;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_grade_list_student, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        c.moveToPosition(position);
        holder.textPid.setText(String.valueOf(c.getInt(c.getColumnIndex("PID"))));
        int choiceGrade = c.getInt(c.getColumnIndex("CHOICEGRADE"));
        int gapGrade = c.getInt(c.getColumnIndex("GAPGRADE"));
        int essayGrade = c.getInt(c.getColumnIndex("ESSAYGRADE"));
        int sumGrade = choiceGrade + gapGrade + essayGrade;
        holder.textChoiceGrade.setText(String.valueOf(choiceGrade));
        holder.textGapGrade.setText(String.valueOf(gapGrade));
        holder.textEssayGrade.setText(String.valueOf(essayGrade));
        Log.i("val", String.valueOf(sumGrade));
       holder.textSumGrade.setText(String.valueOf(sumGrade));
    }

    @Override
    public int getItemCount() {
        return c.getCount();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textPid, textChoiceGrade, textGapGrade, textEssayGrade, textSumGrade;

        public ViewHolder(View itemView) {
            super(itemView);
            textPid = (TextView) itemView.findViewById(R.id.PID);
            textChoiceGrade = (TextView) itemView.findViewById(R.id.grade_choice);
            textGapGrade = (TextView) itemView.findViewById(R.id.grade_gap);
            textEssayGrade = (TextView) itemView.findViewById(R.id.grade_essay);
            textSumGrade = (TextView) itemView.findViewById(R.id.grade_sum);
        }
    }
}
