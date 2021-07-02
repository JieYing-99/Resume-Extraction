package com.app.smarthire;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.app.smarthire.R;
import com.app.smarthire.databinding.ActivityBottomNavigationBinding;
import com.google.android.material.navigation.NavigationView;

public class BottomNavigationActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ActivityBottomNavigationBinding binding;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBottomNavigationBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        //navController = Navigation.findNavController(view);

        setSupportActionBar(binding.toolbarHomePage);
        getSupportActionBar().setTitle("Hire");

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,binding.drawerLayout,binding.toolbarHomePage, R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        SharedPreferences sharedPref = getSharedPreferences("PREF",Context.MODE_PRIVATE);
       sharedPref.getString("USER_ID","");

        navController = Navigation.findNavController(this,R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(binding.bottomNav,navController);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View header= navigationView.getHeaderView(0);
        TextView textViewHeaderName = header.findViewById(R.id.textView_headerName);
        TextView textViewHeaderEmail = header.findViewById(R.id.textView_headerEmail);
        textViewHeaderName.setText(sharedPref.getString("USER_NAME",""));
        textViewHeaderEmail.setText(sharedPref.getString("USER_EMAIL",""));

        binding.navView.setNavigationItemSelectedListener(this);  // if use this, the no need put eg: new View.OnClickListener, but have to implements its class







        //binding.bottomNav.setOnNavigationItemSelectedListener(navListener);

        //I added this if statement to keep the selected fragment when rotating the device
        /*if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new HomeFragment()).commit();
        }*/

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()){
            case R.id.go_to_home:

                navController.navigate(R.id.homeFragment);
                break;

            case R.id.go_to_saved:
                navController.navigate(R.id.savedEmployeeFragment);
                break;

            case R.id.go_to_profile:
                Bundle bundle = getIntent().getExtras();
                navController.navigate(R.id.profileFragment,bundle);
                break;

            case R.id.about_us:
                aboutUsPage();
                break;

            case R.id.feedback:
                createfeedBack();
                break;

            case R.id.contact_us:
                contactUsPage();
                break;

            case R.id.logout:
                logOutConfirmation();
                break;
        }

        binding.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void aboutUsPage(){

        Intent intent = new Intent(BottomNavigationActivity.this, AboutUs.class);
        startActivity(intent);
    }

    private void contactUsPage(){

        Intent intent = new Intent(BottomNavigationActivity.this, ContactUsActivity.class);
        startActivity(intent);

    }

    public void createfeedBack(){

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final EditText edittext = new EditText(this);
        edittext.setSingleLine(false);
        edittext.setPadding(50,30,50,30);
        //edittext.setPadding(10,0,10,0);
        edittext.setHint(R.string.example_feedback);
        alert.setTitle(getString(R.string.feedback));
        alert.setMessage(getString(R.string.enter_your_feedback));

        alert.setView(edittext);

        alert.setPositiveButton(R.string.send, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                String editTextValue = edittext.getText().toString();
                if(editTextValue.isEmpty()){
                    edittext.setError(getString(R.string.feedback_error));
                    errorFeedback();
                }else{
                    showSuccessCustomToast(getString(R.string.feedback_success),Toast.LENGTH_SHORT);
                    //Toast.makeText(BottomNavigationActivity.this,getString(R.string.feedback_success),Toast.LENGTH_SHORT).show();
                    dialog.cancel();
                }

            }
        });

        alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });

        alert.show();

    }

    public void errorFeedback(){

        AlertDialog.Builder errorSendFeedback = new AlertDialog.Builder(this);
        errorSendFeedback.setIcon(R.drawable.error);
        errorSendFeedback.setMessage(getString(R.string.feedback_error));
        errorSendFeedback.setTitle(getString(R.string.error))
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                    }
                });

        AlertDialog alertDialog = errorSendFeedback.create();
        alertDialog.show();
    }

    public void logOutConfirmation(){

        AlertDialog.Builder confirmationLogOut = new AlertDialog.Builder(this);
        confirmationLogOut.setMessage(getString(R.string.confirmation_logout))
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        SharedPreferences sharedPref = getSharedPreferences("PREF", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("USER_ID","LOGGED_OUT");
                        editor.commit();

                        showCustomToast(getString(R.string.logged_out),Toast.LENGTH_SHORT);

                        Intent intent = new Intent(getApplicationContext(), Login.class);
                        startActivity(intent);

                    }
                })

                .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = confirmationLogOut.create();
        alertDialog.show();
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

    private void showSuccessCustomToast(String msg, int length){

        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast_success, findViewById(R.id.custom_toast_container_success));

        TextView text = layout.findViewById(R.id.text);
        text.setText(msg);

        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.BOTTOM, 0, 120);
        toast.setDuration(length);
        toast.setView(layout);
        toast.show();

    }

    /*@Override
    public void onBackPressed() {
        int count = getSupportFragmentManager().getBackStackEntryCount();

        if (count == 0) {
            Toast.makeText(BottomNavigationActivity.this,"Exit",Toast.LENGTH_SHORT).show();
            super.onBackPressed();

            //additional code
        } else {
            getSupportFragmentManager().popBackStack();
        }
    }*/

    /*private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    switch (item.getItemId()) {
                        case R.id.homeFragment:
                            navController.navigate(R.id.homeFragment);
                            //openFragment(new HomeFragment());
                            //selectedFragment = new HomeFragment();
                            return true;

                        case R.id.savedEmployeeFragment:
                            //openFragment(new SavedEmployeeFragment());
                            return true;

                        case R.id.profileFragment:
                            //openFragment(new EmployerProfileFragment());
                            return true;
                    }

                    return false;
                }
            };

    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.nav_host_fragment, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }*/

}
