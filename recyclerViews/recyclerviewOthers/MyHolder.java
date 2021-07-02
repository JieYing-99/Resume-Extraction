package com.app.smarthire.recyclerview;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.smarthire.R;

public class MyHolder extends RecyclerView.ViewHolder {

    ImageView mImageView;
    TextView mName, mPosition;

    public MyHolder(@NonNull View itemView) {
        super(itemView);

        this.mImageView = itemView.findViewById(R.id.imageViewPerson);
        this.mName = itemView.findViewById(R.id.textViewName);
        this.mPosition = itemView.findViewById(R.id.textViewPosition);
    }
}
