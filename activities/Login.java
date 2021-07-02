package com.app.smarthire;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.app.smarthire.R;
import com.app.smarthire.databinding.ActivityLoginBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {
    Button btConfirm,btRegister;
    DatabaseReference reff;
    String userID;
    private ActivityLoginBinding binding;
    private SignInButton signInButton;
    GoogleSignInClient mGoogleSignInClient;
    private String TAG = "Login";
    private FirebaseAuth mAuth;
    private int RC_SIGN_IN = 1;
    private int loginStatus=0;

    LoadingDialog dialog = new LoadingDialog(Login.this);

    User user;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View v = binding.getRoot();

        setContentView(v);

        SharedPreferences sharedPref = getSharedPreferences("PREF",Context.MODE_PRIVATE);


        if (sharedPref.getString("USER_ID","") != null && !sharedPref.getString("USER_ID","").equals("LOGGED_OUT")){
            Intent intent = new Intent(getApplicationContext(), BottomNavigationActivity.class);
            startActivity(intent);
        }


        /*
        btConfirm = findViewById(R.id.button_login);
        btRegister = findViewById(R.id.button_register);
        final EditText editText_username = findViewById(R.id.editText_username);
        final EditText editText_password = findViewById(R.id.editText_password);

         */


        binding.buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){


                //reff = FirebaseDatabase.getInstance().getReference().child("User").child("1");
                reff = FirebaseDatabase.getInstance().getReference().child("User");
                //mAuth = FirebaseAuth.getInstance();
                //userID = mAuth.getCurrentUser().getUid();

                dialog.startLoadingDialog();



                reff.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        loginStatus = 0;
                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                            //Toast.makeText(Main4Activity.this,ds.child("userId").getValue().toString(),Toast.LENGTH_LONG).show();
                            if (binding.editTextUsername.getText().toString().equals(ds.child("userId").getValue().toString())
                                    && binding.editTextPassword.getText().toString().equals(ds.child("password").getValue().toString())){
                                loginStatus = 1;
                                SharedPreferences sharedPref = getSharedPreferences("PREF",Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putString("USER_ID",ds.child("userId").getValue().toString());
                                editor.putString("USER_NAME",ds.child("name").getValue().toString());
                                editor.putString("USER_EMAIL",ds.child("email").getValue().toString());
                                editor.commit();
                                dialog.dissmissDialog();


                                Intent intent = new Intent(getApplicationContext(), BottomNavigationActivity.class);
                                startActivity(intent);
                            }
                        }
                        if (loginStatus!=1){
                            dialog.dissmissDialog();
                            Toast.makeText(Login.this,"Login Failed: Invalid username or password",Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                //

            }

        });

        binding.buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Intent intent = new Intent(Login.this, Register.class);
                startActivity(intent);

            }

        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);

        binding.buttonGoogleSIgnIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                signInGoogle();
            }

        });
    }

    private void signInGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            dialog.startLoadingDialog();

            // Signed in successfully, show authenticated UI.
            reff = FirebaseDatabase.getInstance().getReference().child("User");
            reff.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Boolean gmailFound = false;
                    for (DataSnapshot ds: dataSnapshot.getChildren()){
                        //Toast.makeText(Main4Activity.this,ds.child("userId").getValue().toString(),Toast.LENGTH_LONG).show();
                        if (account.getEmail().equals(ds.child("userId").getValue().toString())){
                            gmailFound = true;
                            SharedPreferences sharedPref = getSharedPreferences("PREF",Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putString("USER_ID",ds.child("userId").getValue().toString());
                            editor.putString("USER_NAME",ds.child("name").getValue().toString());
                            editor.putString("USER_EMAIL",ds.child("email").getValue().toString());
                            editor.commit();

                            dialog.dissmissDialog();

                            mGoogleSignInClient.signOut();
                            Intent intent = new Intent(getApplicationContext(), BottomNavigationActivity.class);

                            startActivity(intent);
                        }
                    }
                    if (!gmailFound){
                        user = new User();
                        user.setUserId(account.getEmail());
                        user.setName(account.getDisplayName());
                        Intent intent = new Intent(getApplicationContext(), Register2B.class);
                        intent.putExtra("Register_UserId",account.getEmail());
                        intent.putExtra("Register_UserName",account.getDisplayName());
                        intent.putExtra("Register_Password","N/A");

                        dialog.dissmissDialog();

                        mGoogleSignInClient.signOut();
                        startActivity(intent);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    dialog.dissmissDialog();

                }
            });



//            SharedPreferences sharedPref = getSharedPreferences("PREF",Context.MODE_PRIVATE);
//            SharedPreferences.Editor editor = sharedPref.edit();
//            editor.putString("USER_ID",account.getEmail());
//            editor.commit();
//
//            Toast.makeText(Login.this,"Welcome" + account.getDisplayName(), Toast.LENGTH_LONG).show();
//            Intent intent = new Intent(Login.this, BottomNavigationActivity.class);
//            startActivity(intent);


        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            dialog.dissmissDialog();
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            Toast.makeText(Login.this,"Failed: " + e.getStatusCode(), Toast.LENGTH_LONG).show();

        }
    }
}
