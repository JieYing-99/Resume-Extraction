package com.app.smarthire;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.app.smarthire.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Register extends AppCompatActivity {
    DatabaseReference reff;
    Button btnConfirm;
    User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivity_register);


        final EditText txtUsername,txtPassword,txtFullName,txtRetype;
        txtUsername = (EditText) findViewById(R.id.editText_username);
        txtFullName = (EditText) findViewById(R.id.editText_fullName);
        txtPassword = (EditText) findViewById(R.id.editText_password);
        txtRetype = (EditText) findViewById(R.id.editText_retype);
        TextView textView_validation = findViewById(R.id.textView_registerValidation);
        textView_validation.setText("");
        btnConfirm = (Button) findViewById(R.id.button_next);

        user = new User();

        reff = FirebaseDatabase.getInstance().getReference().child("User");
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userId = txtUsername.getText().toString();
                String userFullName = txtFullName.getText().toString();
                String passw =  txtPassword.getText().toString();
                String retype =  txtRetype.getText().toString();
//                user.setUserId(userId);
//                user.setPassword(passw);
//                user.setName("Tester");
//                reff.push().setValue(user);
//                Toast.makeText(Register.this,"Successfully registered!",Toast.LENGTH_LONG).show();

                Boolean pass = false;

                if (userId.length()<5){
                    textView_validation.setText("User ID need at least 5 characters");
                } else if (userFullName.length()<5){
                    textView_validation.setText("Full name need at least 5 characters");
                } else if (passw.length()<6){
                    textView_validation.setText("Password need at least 6 characters");
                } else if (!passw.equals(retype)){
                    textView_validation.setText("You retyped password wrongly");
                } else {
                    pass = true;
                }

                if (pass){
                    reff = FirebaseDatabase.getInstance().getReference().child("User");
                    //mAuth = FirebaseAuth.getInstance();
                    //userID = mAuth.getCurrentUser().getUid();



                    reff.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Boolean idTaken = false;
                            for (DataSnapshot ds: dataSnapshot.getChildren()){
                                //Toast.makeText(Main4Activity.this,ds.child("userId").getValue().toString(),Toast.LENGTH_LONG).show();
                                if (userId.equals(ds.child("userId").getValue().toString())){
                                    idTaken = true;
                                    textView_validation.setText("The User ID has been already taken by someone else. Please use another User ID.");

                                }
                            }
                            if (!idTaken){
                                Intent intent = new Intent(getApplicationContext(), Register2.class);
                                intent.putExtra("Register_UserId",userId);
                                intent.putExtra("Register_UserName",userFullName);
                                intent.putExtra("Register_Password",passw);
                                startActivity(intent);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }

            }
        });
    }
}
