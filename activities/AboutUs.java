package com.app.smarthire;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.app.smarthire.R;
import com.app.smarthire.databinding.ActivityAboutUsBinding;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class AboutUs extends AppCompatActivity implements View.OnClickListener {

    private ActivityAboutUsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAboutUsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        setSupportActionBar(binding.toolbarAboutUs);
        getSupportActionBar().setTitle(getString(R.string.aboutus));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //set up up button

        showCustomToast(getString(R.string.aboutus),Toast.LENGTH_LONG);



        Picasso.get()
                .load("https://raw.githubusercontent.com/yujune/Hire/master/screenshots/hire_easy.jpeg")
                .fit()
                .centerCrop()
                .into(binding.imageViewAboutUs, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {
                        showCustomToast(getString(R.string.error_loading_images),Toast.LENGTH_SHORT);

                    }
                });

        Picasso.get()
                .load("https://raw.githubusercontent.com/yujune/Hire/master/screenshots/teeyujune.JPG")
                .fit()
                .centerCrop()
                .into(binding.imageViewTeam1, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {

                        showCustomToast(getString(R.string.error_loading_images),Toast.LENGTH_SHORT);

                    }
                });

        Picasso.get()
                .load("https://raw.githubusercontent.com/yujune/Hire/master/screenshots/pohchongsien.jpeg")
                .fit()
                .centerCrop()
                .into(binding.imageViewTeam2, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {

                        showCustomToast(getString(R.string.error_loading_images),Toast.LENGTH_SHORT);

                    }
                });

        binding.buttonAboutUs.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        Intent intent = new Intent(AboutUs.this, BottomNavigationActivity.class);
        startActivity(intent);
    }

    private void showCustomToast(String msg, int length){

        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast, findViewById(R.id.custom_toast_container));

        TextView text = layout.findViewById(R.id.text);
        text.setText(msg);

        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.BOTTOM, 0, 120);
        toast.setDuration(length);
        toast.setView(layout);
        toast.show();

    }
}
