package com.app.smarthire.recyclerViewSavedEmployee;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.smarthire.CustomItemAnimation;
import com.app.smarthire.Employee;
import com.app.smarthire.recyclerview.MyAdapter;
import com.app.smarthire.R;
import com.app.smarthire.databinding.FragmentSavedEmployeeBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class SavedEmployeeFragment extends Fragment implements MyAdapter.OnItemClickListener {

    private FragmentSavedEmployeeBinding binding;
    private MyAdapter myAdapter;
    private ArrayList<Employee> employees;
    private FirebaseStorage mStorage;
    private DatabaseReference mDatabaseRef;
    private ValueEventListener mDBListener;
    private NavController navController;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSavedEmployeeBinding.inflate(getLayoutInflater(),container,false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setHasOptionsMenu(true);

        employees = new ArrayList<>();

        navController = Navigation.findNavController(view);

        setUpRecyclerView();
        setUpSwipeRecyclerView();
        binding.include.textViewNoItem.setVisibility(TextView.INVISIBLE);

        if(checkConnectivity()){

            binding.progressBarSavedEmployeeFragment.setVisibility(ProgressBar.INVISIBLE);
            mStorage = FirebaseStorage.getInstance();

            mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");

            mDBListener = mDatabaseRef.addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    employees.clear();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        Employee employee = postSnapshot.getValue(Employee.class);
                        employee.setKey(postSnapshot.getKey());

                        if(employee.getBookMark().equals("bookmarked")){
                            employees.add(employee);

                        }

                    }

                    myAdapter.addToeEmployeesFull(employees);

                    myAdapter.notifyDataSetChanged();

                    if(myAdapter.getItemCount()==0){

                        binding.textViewNoSavedEmployee.setVisibility(TextView.VISIBLE);

                    }else{

                        binding.textViewNoSavedEmployee.setVisibility(TextView.INVISIBLE);

                    }

                    //progressCircle.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                    Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    //progressCircle.setVisibility(View.INVISIBLE);

                }
            });
        }else{
            Toast.makeText(getActivity(),"No Internet",Toast.LENGTH_LONG).show();
        }
    }

    public void setUpRecyclerView(){

        binding.include.recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));

        myAdapter = new MyAdapter(getActivity(),employees);

        binding.include.recyclerView.setAdapter(myAdapter);

        binding.include.recyclerView.setItemAnimator(new CustomItemAnimation());

        myAdapter.setOnItemClickListener(this);
    }

    public void setUpSwipeRecyclerView(){

        ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                switch (direction){
                    case ItemTouchHelper.RIGHT:
                        onCallEmployeeClick(viewHolder.getAdapterPosition());
                        myAdapter.notifyDataSetChanged();
                        break;
                    case ItemTouchHelper.LEFT:
                        showDeleteConfirmationDialogForSingleDelete(viewHolder.getAdapterPosition());
                        //Toast.makeText(getActivity(),getString(R.string.delete),Toast.LENGTH_SHORT).show();
                        myAdapter.notifyDataSetChanged();
                        break;
                }
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeLeftBackgroundColor(ContextCompat.getColor(getContext(), R.color.red))
                        .addSwipeRightBackgroundColor(ContextCompat.getColor(getContext(),R.color.green))
                        .addSwipeLeftActionIcon(R.drawable.delete)
                        .addSwipeRightActionIcon(R.drawable.employee_call)
                        .create()
                        .decorate();

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(binding.include.recyclerView);

    }

    public boolean checkConnectivity(){
        ConnectivityManager connectivityManager = (ConnectivityManager)getActivity().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED){
            return true;
        }else{
            return false;
        }
    }

    @Override
    public void onItemClick(View v, int position) {

        //Toast.makeText(getActivity(), "Normal click at position: " + position, Toast.LENGTH_SHORT).show();
        Employee selectedItem = employees.get(position);
        String selectedKey = selectedItem.getKey();
        String name = selectedItem.getmName();
        String employeePosition = selectedItem.getmPosition();
        String employeePhoneNum = selectedItem.getmPhoneNumber();
        String employeeEmail = selectedItem.getmEmail();
        int employeeAge = selectedItem.getmAge();
        String employeeSkills = selectedItem.getmSkills();
        String employeeLanguages = selectedItem.getmLanguages();
        String employeeEducation = selectedItem.getmEducation();
        String employeeQualification = selectedItem.getmQualification();
        String employeeAddress = selectedItem.getmAddress();
        String profilePhoto = selectedItem.getmImageUrl();
        String employeeResume = selectedItem.getResumeImageUrl();
        String verify = selectedItem.getVerify();

        Bundle bundle = new Bundle();
        bundle.putString("EMPLOYEE_NAME",name);
        bundle.putString("EMPLOYEE_POSITION",employeePosition);
        bundle.putString("EMPLOYEE_PHONE",employeePhoneNum);
        bundle.putString("EMPLOYEE_EMAIL",employeeEmail);
        bundle.putInt("EMPLOYEE_AGE",employeeAge);
        bundle.putString("EMPLOYEE_SKILLS",employeeSkills);
        bundle.putString("EMPLOYEE_LANGUAGES",employeeLanguages);
        bundle.putString("EMPLOYEE_EDUCATION",employeeEducation);
        bundle.putString("EMPLOYEE_QUALIFICATION",employeeQualification);
        bundle.putString("EMPLOYEE_PHOTO",profilePhoto);
        bundle.putString("EMPLOYEE_RESUME",employeeResume);
        bundle.putString("EMPLOYEE_ADDRESS",employeeAddress);
        bundle.putString("EMPLOYEE_VERIFY",verify);
        bundle.putString("EMPLOYEE_KEY",selectedKey);


        navController.navigate(R.id.action_savedEmployeeFragment_to_employeeProfile,bundle);

    }

    @Override
    public void onCallEmployeeClick(int position) {

        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + employees.get(position).getmPhoneNumber()));
        startActivity(intent);

    }

    @Override
    public void showDeleteConfirmationDialogForSingleDelete(int position) {
        AlertDialog.Builder confirmationDelete = new AlertDialog.Builder(getActivity());
        confirmationDelete.setMessage(getString(R.string.confirmation_single_delete))
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //saveDeleteAllEmployees();
                        //showSnackBar();
                        onDeleteClick(position);

                    }
                })

                .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = confirmationDelete.create();
        alertDialog.show();

    }

    @Override
    public void onBookmarkedClick(View view, int position) {

        Employee selectedEmployee = employees.get(position);

        if(selectedEmployee.getBookMark().equals("unbookmarked")){

            mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    dataSnapshot.getRef().child(selectedEmployee.getKey()).child("bookMark").setValue("bookmarked");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d("User", databaseError.getMessage());
                }
            });

        }else if (selectedEmployee.getBookMark().equals("bookmarked")){

            mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    dataSnapshot.getRef().child(selectedEmployee.getKey()).child("bookMark").setValue("unbookmarked");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d("User", databaseError.getMessage());
                }
            });

        }


    }

    public void onDeleteClick(int position) {

        binding.progressBarSavedEmployeeFragment.setVisibility(ProgressBar.VISIBLE);
        Toast.makeText(getActivity(),getString(R.string.deleting),Toast.LENGTH_SHORT).show();

        Employee selectedItem = employees.get(position);
        final String selectedKey = selectedItem.getKey();
        if(selectedItem.getmImageUrl().equals("noProfile")||selectedItem.getResumeImageUrl().equals("noResume")){
            mDatabaseRef.child(selectedKey).removeValue();
        }else{
            StorageReference imageRef = mStorage.getReferenceFromUrl(selectedItem.getmImageUrl());
            final StorageReference imageResumeRef = mStorage.getReferenceFromUrl(selectedItem.getResumeImageUrl());

            Log.d("ERROR", "onDeleteClick: "+imageRef);
            imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    imageResumeRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mDatabaseRef.child(selectedKey).removeValue();
                            Toast.makeText(getActivity(),selectedItem.getmName() + " " + getString(R.string.deleted),Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            });
        }

        binding.progressBarSavedEmployeeFragment.setVisibility(ProgressBar.INVISIBLE);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.saved_employee_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.action_search_saved_employee:

                SearchView searchView = (SearchView) item.getActionView();

                searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        myAdapter.getFilter().filter(newText);
                        return false;
                    }
                });
                break;

            case R.id.clear_mark_saved_employee:

                unmarkedAll();

                break;
        }

        return super.onOptionsItemSelected(item);

    }

    private void unmarkedAll(){

        for(int i=0;i<employees.size();i++){

            Employee selectedEmployee = employees.get(i);

            mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    dataSnapshot.getRef().child(selectedEmployee.getKey()).child("bookMark").setValue("unbookmarked");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d("User", databaseError.getMessage());
                }
            });
        }

    }

}
