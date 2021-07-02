package com.app.smarthire.recyclerviewEducation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.smarthire.R;

import java.util.List;

public class EducationAdapter extends RecyclerView.Adapter<EducationAdapter.EducationHolder> {

    private List<Education> education;

    public EducationAdapter(List<Education> education) {
        this.education = education;
    }

    @NonNull
    @Override
    public EducationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.education_view_design,parent,false);
        return new EducationHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EducationHolder holder, int position) {
        Education educationCurrent = education.get(position);
        holder.textViewRecylerEduPostion.setText(Integer.toString(position+1));
        holder.textViewRecylerEduName.setText(educationCurrent.getEducation());
        holder.buttonRecylerEduDelete.setOnClickListener(v -> {
            education.remove(educationCurrent);
            notifyDataSetChanged();
        });

    }

    @Override
    public int getItemCount() {
        return education.size();
    }

    public class EducationHolder extends RecyclerView.ViewHolder{

        TextView textViewRecylerEduPostion, textViewRecylerEduName;
        Button buttonRecylerEduDelete;

        public EducationHolder(@NonNull View itemView) {
            super(itemView);

            textViewRecylerEduPostion = itemView.findViewById(R.id.textViewRecyclerEduPosition);
            textViewRecylerEduName = itemView.findViewById(R.id.textViewRecylerEduName);
            buttonRecylerEduDelete = itemView.findViewById(R.id.buttonRecylerEduDelete);
        }
    }
}
