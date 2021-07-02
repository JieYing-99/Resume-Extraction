package com.app.smarthire.recylerviewSkills;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.smarthire.R;

import java.util.List;

public class SkillsAdapter extends RecyclerView.Adapter<SkillsAdapter.SkillsHolder> {

    private List<Skills> skills;

    public SkillsAdapter(List<Skills> skills) {
        this.skills = skills;
    }

    @NonNull
    @Override
    public SkillsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.skills_view_design,parent,false);
        return new SkillsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SkillsHolder holder, int position) {
        Skills currentSkills = skills.get(position);
        holder.textViewHolderSkillsPosition.setText(Integer.toString(position+1));
        holder.textViewHolderSkillsSkills.setText(currentSkills.getSkill());
        holder.textViewHolderSkillsLevel.setText(currentSkills.getLevel());
        holder.buttonHolderSkillsDelete.setOnClickListener(v -> {
            skills.remove(currentSkills);
            notifyDataSetChanged();
        });

    }

    @Override
    public int getItemCount() {
        return skills.size();
    }

    public class SkillsHolder extends RecyclerView.ViewHolder{

        TextView textViewHolderSkillsPosition, textViewHolderSkillsSkills,textViewHolderSkillsLevel;
        Button buttonHolderSkillsDelete;

        public SkillsHolder(@NonNull View itemView) {
            super(itemView);

            textViewHolderSkillsPosition = itemView.findViewById(R.id.textViewHolderSkillsPosition);
            textViewHolderSkillsSkills = itemView.findViewById(R.id.textViewHolderSkillsSkills);
            textViewHolderSkillsLevel = itemView.findViewById(R.id.textViewHolderSkillsLevel);
            buttonHolderSkillsDelete = itemView.findViewById(R.id.buttonHolderSkillsDelete);
        }
    }
}


