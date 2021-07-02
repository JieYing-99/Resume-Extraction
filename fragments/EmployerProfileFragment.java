package com.app.smarthire.frgaments;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.app.smarthire.databinding.FragmentProfileBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.Calendar;

public class EmployerProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    DatabaseReference reff;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(getLayoutInflater(), container, false);

        View view = binding.getRoot();

        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SharedPreferences sharedPref = requireActivity().getSharedPreferences("PREF",Context.MODE_PRIVATE);


        binding.textViewProfileName.setText(sharedPref.getString("USER_ID",""));

        String userId = sharedPref.getString("USER_ID","");

        reff = FirebaseDatabase.getInstance().getReference().child("User");
        //mAuth = FirebaseAuth.getInstance();
        //userID = mAuth.getCurrentUser().getUid();



        reff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    for (DataSnapshot ds: dataSnapshot.getChildren()){
                        if (userId.equals(ds.child("userId").getValue().toString())){
                            int currentYear = Calendar.getInstance().get(Calendar.YEAR);

                            int age = currentYear - Integer.parseInt(ds.child("birthYear").getValue().toString()) + 1;

                            binding.textViewProfileName.setText(ds.child("name").getValue().toString());
                            binding.textViewProfilePosition.setText(ds.child("position").getValue().toString());
                            binding.textViewProfilePhoneNum.setText(ds.child("contact").getValue().toString());
                            binding.textViewProfileEmail.setText(ds.child("email").getValue().toString());
                            binding.textViewProfileAddress.setText(ds.child("address").getValue().toString());
                            binding.textViewProfileAge.setText(age+"");

                            String data = ds.child("faceImage").getValue().toString();

                            if (!data.equals("N/A")){
                                Uri imageUri  =  Uri.parse(data);

//                                FirebaseStorage storage = FirebaseStorage.getInstance();
//                                StorageReference imageRef = storage.getReferenceFromUrl(imageUri.toString());
//
//                                imageRef.getBytes(4096*4096)
//                                        .addOnSuccessListener(new OnSuccessListener<byte[]>() {
//                                            @Override
//                                            public void onSuccess(byte[] bytes) {
//                                                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
//                                                binding.imageViewEmployeeProfile.setImageBitmap(bitmap);
//
//                                            }
//                                        });
                                Picasso.get()
                                        .load(data)
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



                        }
                    }
                } catch (Exception e) {
                    //Toast.makeText(getContext(),e.toString(),Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

}
