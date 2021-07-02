package com.app.smarthire.frgaments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.app.smarthire.Step1Activity;
import com.ceylonlabs.imageviewpopup.ImagePopup;
import com.app.smarthire.R;
import com.app.smarthire.databinding.ActivityEmployeeProfileBinding;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;


/**
 * A simple {@link Fragment} subclass.
 */
public class EmployeeProfileFragment extends Fragment {

    private static final String ARG_KITTEN_NUMBER = "argKittenNumber";


    public static EmployeeProfileFragment newInstance(int kittenNumber) {
        Bundle args = new Bundle();
        args.putInt(ARG_KITTEN_NUMBER, kittenNumber);

        EmployeeProfileFragment fragment = new EmployeeProfileFragment();
        fragment.setArguments(args);

        return fragment;
    }

    private ActivityEmployeeProfileBinding binding;
    private ConnectivityManager connectivityManager;

    private String employeePhone, employeeName, employeePosition, employeeEmail, employeeAddress, employeeSkills, employeeLanguages, employeeEducation, employeeQualification, employeeProfilePhoto,
            employeeResume,verify, selectedKey;
    private int employeeAge;

    private DatabaseReference mDatabaseRef;
    private ValueEventListener mDBListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = ActivityEmployeeProfileBinding.inflate(getLayoutInflater(), container, false);

        View view = binding.getRoot();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");

        //Bundle args = getArguments();
        //int kittenNumber = args.containsKey(ARG_KITTEN_NUMBER) ? args.getInt(ARG_KITTEN_NUMBER) : 1;



        connectivityManager = (ConnectivityManager) getActivity().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);


        Bundle bundle = getArguments();

        employeeName = bundle.getString("EMPLOYEE_NAME");
        employeePosition = bundle.getString("EMPLOYEE_POSITION");
        employeePhone = bundle.getString("EMPLOYEE_PHONE");
        employeeEmail = bundle.getString("EMPLOYEE_EMAIL");
        employeeAddress = bundle.getString("EMPLOYEE_ADDRESS");
        employeeAge = bundle.getInt("EMPLOYEE_AGE", 0);
        employeeSkills = bundle.getString("EMPLOYEE_SKILLS");
        employeeLanguages = bundle.getString("EMPLOYEE_LANGUAGES");
        employeeEducation = bundle.getString("EMPLOYEE_EDUCATION");
        employeeQualification = bundle.getString("EMPLOYEE_QUALIFICATION");
        employeeProfilePhoto = bundle.getString("EMPLOYEE_PHOTO");
        employeeResume = bundle.getString("EMPLOYEE_RESUME");
        verify = bundle.getString("EMPLOYEE_VERIFY");
        selectedKey = bundle.getString("EMPLOYEE_KEY");
        //Log.d("VERIFICATION", "onViewCreated: " + verify);

        if(verify.equals("verified")){

            binding.buttonProfileVerify.setText(getString(R.string.verified));
            binding.buttonProfileVerify.setClickable(false);
            binding.imageViewVerified.setVisibility(ImageView.VISIBLE);

        }


        if(employeeProfilePhoto.equals("noProfile")){
            binding.imageViewEmployeeProfile.setImageResource(R.drawable.ic_person);

        }else{
            Picasso.get()
                    .load(employeeProfilePhoto)
                    .fit()
                    .centerCrop()
                    .into(binding.imageViewEmployeeProfile, new Callback() {
                        @Override
                        public void onSuccess() {
                            //Toast.makeText(getActivity(), "Image Loaded Successfully", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(Exception e) {
                            Toast.makeText(getActivity(), "Error loading image", Toast.LENGTH_SHORT).show();
                        }
                    });
        }


        //binding.imageViewEmployeeProfile.setImageURI(Uri.parse(employeeProfilePhoto));

        binding.textViewProfileName.setText(employeeName);
        binding.textViewProfilePosition.setText(employeePosition);
        binding.textViewProfileEmail.setText(employeeEmail);
        binding.textViewProfileAge.setText(Integer.toString(employeeAge));
        binding.textViewProfilePhoneNum.setText(employeePhone);
        binding.textViewProfileAddress.setText(employeeAddress);
        binding.textViewProfileSkills.setText(employeeSkills);
        binding.textViewProfileLanguages.setText(employeeLanguages);
        binding.textViewProfileEducation.setText(employeeEducation);
        binding.textViewProfileQualification.setText(employeeQualification);

        binding.fabProfileCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callIntent();
            }
        });

        binding.fabProfileMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messageIntent();
            }
        });

        binding.fabResume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inflateDialogResume();
            }
        });

        binding.buttonProfileVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyEmployee();
            }
        });


    }

    private void verifyEmployee(){

//        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                dataSnapshot.getRef().child(selectedKey).child("verify").setValue("verified");
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Log.d("User", databaseError.getMessage());
//            }
//        });
//
//        binding.buttonProfileVerify.setText(getString(R.string.verified));

//        binding.imageViewVerified.setVisibility(ImageView.VISIBLE);

        if(!verify.equals("verified")){

            Intent intent = new Intent(getContext(), Step1Activity.class);
            if (employeeProfilePhoto.equals("noProfile")){
                intent.putExtra("employeeResume","N/A");
            } else {
                intent.putExtra("employeeResume","N/A");

            }
            if (employeeResume.equals("noResume")){
                intent.putExtra("employeeFace","N/A");
            } else {
                intent.putExtra("employeeFace",employeeResume);

            }
            intent.putExtra("employeeKey",selectedKey);

            startActivity(intent);

        }



    }

    private void inflateDialogResume() {
        //Toast.makeText(getActivity(), "Loading resume", Toast.LENGTH_SHORT).show();
        showCustomToast(getString(R.string.loading_resume),Toast.LENGTH_SHORT);
        final ImagePopup imagePopup = new ImagePopup(getActivity());
        //imagePopup.setWindowHeight(800);
        imagePopup.setFullScreen(true);
        imagePopup.setBackgroundColor(Color.WHITE);
        //imagePopup.setWindowWidth(650);
        imagePopup.setImageOnClickClose(true);
        if(employeeResume!=" "){
            imagePopup.initiatePopupWithPicasso(employeeResume);
        }else{
            imagePopup.initiatePopup(getResources().getDrawable(R.drawable.error));
        }
        imagePopup.viewPopup();
        /*AlertDialog.Builder imageDialog = new AlertDialog.Builder(getActivity());
        //ImageView showImage = new ImageView(getActivity());
        //imageDialog.setTitle("Resume");
        View dialogLayout = getLayoutInflater().inflate(R.layout.dialog_resume, null);
        ImageView imageView = dialogLayout.findViewById(R.id.imageViewDialogResume);
        imageView.setImageURI(Uri.parse(employeeProfilePhoto));
        imageDialog.setView(dialogLayout);
        imageDialog.show();*/


    }

    private void callIntent() {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + employeePhone));
        startActivity(intent);

    }

    private void messageIntent() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, employeeEmail);
        intent.putExtra(Intent.EXTRA_TEXT, "Hi, " + employeeName + ". ");
        intent.setType("text/plain");
        startActivity(intent);

    }

    private void showCustomToast(String msg, int length){

        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast, getActivity().findViewById(R.id.custom_toast_container));

        TextView text = layout.findViewById(R.id.text);
        text.setText(msg);

        Toast toast = new Toast(getActivity().getApplicationContext());
        toast.setGravity(Gravity.BOTTOM, 0, 120);
        toast.setDuration(length);
        toast.setView(layout);
        toast.show();

    }


    private void showSuccessCustomToast(String msg, int length){

        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast_success, getActivity().findViewById(R.id.custom_toast_container_success));

        TextView text = layout.findViewById(R.id.text);
        text.setText(msg);

        Toast toast = new Toast(getActivity().getApplicationContext());
        toast.setGravity(Gravity.BOTTOM, 0, 120);
        toast.setDuration(length);
        toast.setView(layout);
        toast.show();

    }


}
