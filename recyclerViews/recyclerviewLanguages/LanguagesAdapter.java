package com.app.smarthire.recyclerviewLanguages;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.smarthire.R;

import java.util.List;

public class LanguagesAdapter extends RecyclerView.Adapter<LanguagesAdapter.LanguagesHolder> {

    private List<Languages> languages;

    public LanguagesAdapter(List<Languages> languages) {
        this.languages = languages;
    }

    @NonNull
    @Override
    public LanguagesAdapter.LanguagesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.languages_view_design,parent,false);
        return new LanguagesAdapter.LanguagesHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LanguagesAdapter.LanguagesHolder holder, int position) {
        Languages currentLanguages = languages.get(position);
        holder.textViewHolderLanguagesPosition.setText(Integer.toString(position+1));
        holder.textViewHolderLanguagesLanguages.setText(currentLanguages.getLanguage());
        holder.textViewHolderLanguagesLevel.setText(currentLanguages.getLevel());
        holder.buttonHolderLanguagesDelete.setOnClickListener(v -> {
            languages.remove(currentLanguages);
            notifyDataSetChanged();
        });

    }

    @Override
    public int getItemCount() {
        return languages.size();
    }

    public class LanguagesHolder extends RecyclerView.ViewHolder{

        TextView textViewHolderLanguagesPosition, textViewHolderLanguagesLanguages,textViewHolderLanguagesLevel;
        Button buttonHolderLanguagesDelete;

        public LanguagesHolder(@NonNull View itemView) {
            super(itemView);

            textViewHolderLanguagesPosition = itemView.findViewById(R.id.textViewHolderLanguagesPosition);
            textViewHolderLanguagesLanguages = itemView.findViewById(R.id.textViewHolderLanguagesLanguages);
            textViewHolderLanguagesLevel = itemView.findViewById(R.id.textViewHolderLanguagesLevel);
            buttonHolderLanguagesDelete = itemView.findViewById(R.id.buttonHolderLanguagesDelete);
        }
    }
}
