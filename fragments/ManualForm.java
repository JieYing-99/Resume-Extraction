package com.app.smarthire.frgaments;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.smarthire.BottomNavigationActivity;
import com.app.smarthire.Employee;
import com.app.smarthire.recyclerviewEducation.Education;
import com.app.smarthire.recyclerviewEducation.EducationAdapter;
import com.app.smarthire.recyclerviewLanguages.Languages;
import com.app.smarthire.recyclerviewLanguages.LanguagesAdapter;
import com.app.smarthire.recylerviewSkills.Skills;
import com.app.smarthire.recylerviewSkills.SkillsAdapter;
import com.app.smarthire.R;
import com.app.smarthire.databinding.FragmentManualFormBinding;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Pattern;

import static com.app.smarthire.R.string.address_empty_error;

//implements AdapterView.OnItemSelectedListener

public class ManualForm extends Fragment {

    private FragmentManualFormBinding binding;
    private RadioButton radioButton;
    private ArrayList<Skills> skills;
    private ArrayList<Languages> languages;
    private ArrayList<Education> educations;
    private SkillsAdapter skillsAdapter;
    private LanguagesAdapter languagesAdapter;
    private EducationAdapter educationAdapter;

    private int radioId = 0, undoRadioId = 0, profileOrResume = 0,spinnerSelectedPostion=0,spinnerSelectedSkills=0,spinnerSelectedLanguages=0, spinnerSelectedPhone=0;
    private boolean isCheckBoxChecked;

    private String undoName, undoPhone, undoEmail, undoAge, undoAddress, undoQualification;
    private String employeeSkills = "", employeeLanguages = "", employeeEducation = "", name, phone, email, age, position, address, qualification;
    private ArrayList<Skills> undoSkills;
    private ArrayList<Languages> undoLanguages;
    private ArrayList<Education> undoEducations;

    private String camaraPermission[];
    private String storagePermission[];

    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 400;
    private static final int IMAGE_PICK_GALLERY_CODE = 1000;
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    private Uri imageUri, resultUri, undoProfileUri, resumeUri, profileUri, undoResumeUri,photoDownloadUri,resumeDownloadUri;

    private DatabaseReference mDatabaseRef;
    private StorageReference mStorageRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentManualFormBinding.inflate(getLayoutInflater(), container, false);
        View view = binding.getRoot();
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        skills = new ArrayList<>();
        languages = new ArrayList<>();
        educations = new ArrayList<>();
        camaraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");
        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");

        //set up skills recyler view
        setUpRecyclerSkills();

        //set up languages recycler view
        setUpRecyclerLanguages();

        //set up education recyler view
        setUpRecylerEducation();

        //set up phone spinner
        setUpPhoneSpinner();

        //set up position spinner
        setUpPositionSpinner();

        //set up level spinner
        setUpLevelSpinner();

        binding.fabManualEditProfile.setOnClickListener(v -> {
            profileOrResume = 1;
            setUpAlertDialog();
        });

        binding.buttonAddManualSkills.setOnClickListener(v -> {
            Skills newSkills = new Skills(binding.editTextManualSkills.getText().toString(), binding.spinnerSkills.getSelectedItem().toString());
            skills.add(newSkills);
            skillsAdapter.notifyDataSetChanged();
            binding.editTextManualSkills.getText().clear();
        });

        binding.buttonAddManualLanguages.setOnClickListener(v -> {
            Languages newLanguages = new Languages(binding.editTextManualLanguages.getText().toString(), binding.spinnerLanguages.getSelectedItem().toString());
            languages.add(newLanguages);
            languagesAdapter.notifyDataSetChanged();
            binding.editTextManualLanguages.getText().clear();
        });

        binding.buttonAddManualEducation.setOnClickListener(v -> {
            Education newEducation = new Education(binding.editTextManualEducation.getText().toString());
            educations.add(newEducation);
            educationAdapter.notifyDataSetChanged();
            binding.editTextManualEducation.getText().clear();
        });

        binding.buttonManualUploadResume.setOnClickListener(v -> {
            profileOrResume = 2;
            setUpAlertDialog();
        });

        binding.buttonManualDone.setOnClickListener(v -> {
            if (isEveryInputFilled()) {
                if (isGenderAndBoxChecked()) {
                    postDataToDatabase();
                }
            } else {
                //Toast.makeText(getActivity(), getString(R.string.error_input), Toast.LENGTH_LONG).show();
                showCustomToast(getString(R.string.error_input), Toast.LENGTH_LONG);
            }
        });

        binding.spinnerPosition.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position == 4){
                    binding.editTextManualPosition.setVisibility(EditText.VISIBLE);
                }else{
                    binding.editTextManualPosition.setVisibility(EditText.INVISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.manual_form_overflow, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.clearAll:
                saveClear();
                clearAll();
                showSnackBar();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public void setUpAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Pick your options");
        builder.setItems(getResources().getStringArray(R.array.take_photo_options), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int option) {
                if (option == 0) {
                    if (!checkCameraPermission()) {
                        requestCameraPermission();
                    } else {
                        dispatchTakePictureIntent();
                    }
                } else {
                    showCustomToast(getString(R.string.pick_photo),Toast.LENGTH_LONG);
                    //Toast.makeText(getActivity(), "Pick a photo", Toast.LENGTH_SHORT).show();
                    if (!checkStoragePermission()) {
                        requestStoragePermission();
                    } else {
                        pickGallery();
                    }
                }
                // the user clicked on colors[which]
            }
        });
        builder.show();
    }

    /*
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String text = parent.getItemAtPosition(position).toString();
            if (parent.getId() == R.id.spinnerSkills) {
                Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }*/

    public void setUpRecyclerSkills() {
        binding.recyclerViewSkills.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        skillsAdapter = new SkillsAdapter(skills);
        binding.recyclerViewSkills.setAdapter(skillsAdapter);
    }

    public void setUpRecyclerLanguages() {
        binding.recyclerViewLanguages.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        languagesAdapter = new LanguagesAdapter(languages);
        binding.recyclerViewLanguages.setAdapter(languagesAdapter);
    }

    public void setUpRecylerEducation() {
        binding.recyclerViewEducation.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        educationAdapter = new EducationAdapter(educations);
        binding.recyclerViewEducation.setAdapter(educationAdapter);
    }

    public void setUpPhoneSpinner() {
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(getActivity(), R.array.phone, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerPhone.setAdapter(adapter1);
        //binding.spinnerPhone.setOnItemSelectedListener(this);
    }

    public void setUpPositionSpinner(){
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.position, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerPosition.setAdapter(adapter);
    }

    public void setUpLevelSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.level, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerSkills.setAdapter(adapter);
        binding.spinnerLanguages.setAdapter(adapter); //TODO: NOT SURE
        //binding.spinnerSkills.setOnItemSelectedListener(this);
    }

    public void saveClear() {
        undoName = binding.editTextManualName.getText().toString();
        undoPhone = binding.editTextManualPhoneNum.getText().toString();
        undoEmail = binding.editTextManualEmail.getText().toString();
        undoAge = binding.editTextManualAge.getText().toString();
        if (binding.radioMale.isChecked() || binding.radioFemale.isChecked()) {
            radioId = binding.radioGroupGender.getCheckedRadioButtonId();
        } else {
            radioId = 0;
        }
        undoRadioId = radioId;
        undoAddress = binding.editTextManualAddress.getText().toString();
        undoQualification = binding.editTextManualQualification.getText().toString();
        undoSkills = new ArrayList<>(skills);
        undoLanguages = new ArrayList<>(languages);
        undoEducations = new ArrayList<>(educations);
        isCheckBoxChecked = binding.checkBoxManualTerms.isChecked();
        undoProfileUri = profileUri;
        undoResumeUri = resumeUri;
        spinnerSelectedPhone = binding.spinnerPhone.getSelectedItemPosition();
        spinnerSelectedPostion = binding.spinnerPosition.getSelectedItemPosition();
        spinnerSelectedSkills = binding.spinnerSkills.getSelectedItemPosition();
        spinnerSelectedLanguages = binding.spinnerLanguages.getSelectedItemPosition();
        position = binding.editTextManualPosition.getText().toString();
    }

    public void undo(View view) {
        binding.editTextManualName.setText(undoName);
        binding.editTextManualPhoneNum.setText(undoPhone);
        binding.editTextManualEmail.setText(undoEmail);
        binding.editTextManualAge.setText(undoAge);
        if (!(undoRadioId == 0)) {
            radioButton = view.findViewById(undoRadioId);
            radioButton.setChecked(true);
            //Toast.makeText(getActivity(), radioButton.getText(), Toast.LENGTH_SHORT).show();
        }
        binding.editTextManualAddress.setText(undoAddress);
        binding.editTextManualQualification.setText(undoQualification);
        skills.addAll(undoSkills);
        languages.addAll(undoLanguages);
        educations.addAll(undoEducations);
        skillsAdapter.notifyDataSetChanged();
        languagesAdapter.notifyDataSetChanged();
        educationAdapter.notifyDataSetChanged();
        binding.checkBoxManualTerms.setChecked(isCheckBoxChecked);
        if (undoProfileUri != null) {
            profileUri = undoProfileUri;
            binding.imageViewManualProfile.setImageURI(undoProfileUri);
        }

        if (undoResumeUri != null) {
            resumeUri = undoResumeUri;
            binding.textViewManualUploadResumeMsg.setVisibility(TextView.VISIBLE);
        }

        binding.spinnerSkills.setSelection(spinnerSelectedSkills);
        binding.spinnerLanguages.setSelection(spinnerSelectedLanguages);
        binding.spinnerPhone.setSelection(spinnerSelectedPhone);
        binding.spinnerPosition.setSelection(spinnerSelectedPostion);
        if(spinnerSelectedPostion == 4){
            binding.editTextManualPosition.setVisibility(EditText.VISIBLE);
            binding.editTextManualPosition.setText(position);
        }

    }

    public void clearAll() {
        binding.editTextManualName.getText().clear();
        binding.editTextManualPhoneNum.getText().clear();
        binding.editTextManualEmail.getText().clear();
        binding.editTextManualAge.getText().clear();
        binding.radioGroupGender.clearCheck();
        binding.editTextManualPosition.getText().clear();
        binding.editTextManualPosition.setVisibility(EditText.INVISIBLE);
        //binding.radioFemale.setChecked(false);
        //binding.radioMale.setChecked(false);
        binding.editTextManualAddress.getText().clear();
        binding.editTextManualQualification.getText().clear();
        binding.editTextManualSkills.getText().clear();
        binding.editTextManualLanguages.getText().clear();
        binding.editTextManualEducation.getText().clear();
        skills.removeAll(skills);
        languages.removeAll(languages);
        educations.removeAll(educations);
        skillsAdapter.notifyDataSetChanged();
        languagesAdapter.notifyDataSetChanged();
        educationAdapter.notifyDataSetChanged();
        binding.checkBoxManualTerms.setChecked(false);
        profileUri = null;
        resumeUri = null;
        binding.imageViewManualProfile.setImageDrawable(getResources().getDrawable(R.drawable.resume_upload));
        binding.textViewManualUploadResumeMsg.setVisibility(TextView.INVISIBLE);
        binding.spinnerPosition.setSelection(0);
        binding.spinnerPhone.setSelection(0);
        binding.spinnerSkills.setSelection(0);
        binding.spinnerLanguages.setSelection(0);
    }

    public boolean isEveryInputFilled() {

        boolean validInput = true;

        if (binding.editTextManualName.getText().toString().isEmpty()) {
            binding.editTextManualName.setError(getString(R.string.name_empty_error));
            validInput = false;
        }

        if (binding.editTextManualAddress.getText().toString().isEmpty()) {
            binding.editTextManualAddress.setError(getString(address_empty_error));
            validInput = false;
        }

        if (binding.editTextManualQualification.getText().toString().isEmpty()) {
            binding.editTextManualQualification.setError(getString(R.string.qualification_empty_error));
            validInput = false;
        }

        if (binding.editTextManualEmail.getText().toString().isEmpty()) {
            binding.editTextManualEmail.setError(getString(R.string.email_empty_error));
            validInput = false;


        } else {
            String emailRegex = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"" +
                    "(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])" +
                    "*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|" +
                    "[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:" +
                    "(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";

            Pattern pattern = Pattern.compile(emailRegex);

            if (!pattern.matcher(binding.editTextManualEmail.getText().toString()).matches()) {
                binding.editTextManualEmail.setError(getString(R.string.email_format_error));
                validInput = false;

            }
        }

        if (binding.editTextManualPhoneNum.getText().toString().isEmpty()) {
            binding.editTextManualPhoneNum.setError(getString(R.string.phone_empty_error));
            validInput = false;

        } else {
            if (!isNumeric(binding.editTextManualPhoneNum.getText().toString())) {
                binding.editTextManualPhoneNum.setError(getString(R.string.numeric_phone_error));
                validInput = false;

            }else{
                if (binding.editTextManualPhoneNum.getText().toString().length()>10 || binding.editTextManualPhoneNum.getText().toString().length()<9){
                    binding.editTextManualPhoneNum.setError(getString(R.string.invalid_phone_length));
                    validInput = false;
                }
            }
        }

        if(binding.spinnerPosition.getSelectedItem().toString().equals("Others")){
            if(binding.editTextManualPosition.getText().toString().isEmpty()){
                binding.editTextManualPosition.setError(getString(R.string.empty_position));
                validInput = false;
            }
        }

        if (binding.editTextManualAge.getText().toString().isEmpty()) {
            binding.editTextManualAge.setError(getString(R.string.age_empty_error));
            validInput = false;

        } else {
            if (!isNumeric(binding.editTextManualAge.getText().toString())) {
                binding.editTextManualAge.setError(getString(R.string.numeric_age_error));
                validInput = false;
            }else{
                if(Integer.parseInt(binding.editTextManualAge.getText().toString())>50||Integer.parseInt(binding.editTextManualAge.getText().toString())<18){
                    binding.editTextManualAge.setError(getString(R.string.invalid_age));
                }
            }
        }

        if (validInput == false) {
            return false;
        } else {
            return true;
        }
    }

    public boolean isGenderAndBoxChecked() {
        if (binding.radioMale.isChecked() == false && binding.radioFemale.isChecked() == false) {
            //Toast.makeText(getActivity(), R.string.gender_error, Toast.LENGTH_SHORT).show();
            showCustomToast(getString(R.string.gender_error), Toast.LENGTH_SHORT);
            return false;
        } else {
            if (binding.checkBoxManualTerms.isChecked()) {
                return true;
            } else {
                showCustomToast(getString(R.string.unchecked_TandC), Toast.LENGTH_SHORT);
                //Toast.makeText(getActivity(), R.string.unchecked_TandC, Toast.LENGTH_SHORT).show();
                return false;
            }
        }
    }

    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            int number = Integer.parseInt(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    private void postDataToDatabase() {
        binding.progressBarManual.setVisibility(ProgressBar.VISIBLE);

        if(skills.size()>0){
            for (int i = 0; i < skills.size(); i++) {
                employeeSkills += i + 1 + " " + skills.get(i).toString();
            }
        }else{
            employeeSkills = getString(R.string.no_skills);
        }

        if(languages.size()>0){
            for (int i = 0; i < languages.size(); i++) {
                employeeLanguages += i + 1 + " " + languages.get(i).toString();
            }
        }else{
            employeeLanguages = getString(R.string.no_languages);
        }

        if(educations.size()>0){
            for (int i = 0; i < educations.size(); i++) {
                employeeEducation += i + 1 + " " + educations.get(i).toString();
            }
        }else{
            employeeEducation = getString(R.string.no_education);
        }

        if(binding.spinnerPosition.getSelectedItemPosition()==4){
            position = binding.editTextManualPosition.getText().toString();
        }else{
            position = binding.spinnerPosition.getSelectedItem().toString();
        }

        name = binding.editTextManualName.getText().toString();
        phone = binding.spinnerPhone.getSelectedItem().toString() + binding.editTextManualPhoneNum.getText().toString();
        email = binding.editTextManualEmail.getText().toString();
        age = binding.editTextManualAge.getText().toString();
        address = binding.editTextManualAddress.getText().toString();
        qualification = binding.editTextManualQualification.getText().toString();

        if (profileUri != null) {
            final StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(profileUri));

            fileReference.putFile(profileUri).continueWithTask(
                    new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException(); }
                            return fileReference.getDownloadUrl();
                        } })
                    .addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {

                                photoDownloadUri = task.getResult();

                            }
                            else {
                                //Toast.makeText(getActivity(), "upload failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                showCustomToast(getString(R.string.error)+" : "+ task.getException().getMessage(), Toast.LENGTH_LONG);
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            showCustomToast(e.getMessage(), Toast.LENGTH_LONG);
                            //Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        } else {
            photoDownloadUri = Uri.parse("noProfile");
            Toast.makeText(getActivity(), "No profile selected", Toast.LENGTH_SHORT).show();
        }

        if (resumeUri != null) {
            final StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(resumeUri));

            fileReference.putFile(resumeUri).continueWithTask(
                    new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException(); }
                            return fileReference.getDownloadUrl();
                        } })
                    .addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {

                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        binding.progressBarManual.setVisibility(ProgressBar.INVISIBLE);
                                    }
                                }, 200);

                                resumeDownloadUri = task.getResult();
                                saveToDatabase(name, address, phone, email, employeeSkills, employeeLanguages, employeeEducation, qualification, age, position);
                            }
                            else {
                                //Toast.makeText(getActivity(), "upload failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                showCustomToast(getString(R.string.error)+" : "+ task.getException().getMessage(), Toast.LENGTH_LONG);
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                            showCustomToast(e.getMessage(), Toast.LENGTH_LONG);
                        }
                    });
        } else {
            resumeDownloadUri = Uri.parse("noResume");
            saveToDatabase(name, address, phone, email, employeeSkills, employeeLanguages, employeeEducation, qualification, age, position); //TODO: solved bug
            Toast.makeText(getActivity(), "No resume selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveToDatabase(String name, String address, String phone, String email, String finalEmployeeSkills, String finalEmployeeLanguages, String finalEmployeeEducation, String qualification, String age, String position) {
        String timeStamp = new SimpleDateFormat("dd-MM-yyyy HH:mm").format(new Date());
        Employee upload = new Employee(name, photoDownloadUri.toString(),resumeDownloadUri.toString(),address,phone,email,timeStamp, finalEmployeeSkills, finalEmployeeLanguages,
                finalEmployeeEducation,qualification,Integer.parseInt(age),position);
        String uploadId = mDatabaseRef.push().getKey();
        mDatabaseRef.child(uploadId).setValue(upload);
        //mDatabaseRef.push().setValue(upload);
        showSuccessCustomToast(getString(R.string.upload_success),Toast.LENGTH_LONG);
        //Toast.makeText(getActivity(), "Upload successful", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(getActivity(), BottomNavigationActivity.class);
        startActivity(intent);
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    public void showSnackBar() {
        Snackbar snackbar = Snackbar.make(binding.coordinateLayoutManualForm, R.string.snackbar_delete, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.undo, v -> {
                    Snackbar snackbarUndo = Snackbar.make(binding.coordinateLayoutManualForm, R.string.undo_success, Snackbar.LENGTH_SHORT);
                    snackbarUndo.show();
                    undo(getView());
                });
        snackbar.show();
    }

    private boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    private boolean checkStoragePermission() {
        boolean result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(getActivity(), camaraPermission, CAMERA_REQUEST_CODE);
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {

            ContentValues values = new ContentValues();
            //Toast.makeText(getActivity(), "Say cheese!", Toast.LENGTH_SHORT).show();
            showCustomToast(getString(R.string.say_cheese),Toast.LENGTH_LONG);
            values.put(MediaStore.Images.Media.TITLE, "NewPic");
            values.put(MediaStore.Images.Media.DESCRIPTION, "Image to Text");
            imageUri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(getActivity(), storagePermission, STORAGE_REQUEST_CODE);
    }

    private void pickGallery() {
        //intent to pick image from gallery
        Intent intent = new Intent(Intent.ACTION_PICK);
        //set intent type to image
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);

    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    Log.d("myTag", "Camera:" + cameraAccepted + " Storage:" + writeStorageAccepted);
                    if (cameraAccepted && writeStorageAccepted) {
                        dispatchTakePictureIntent();
                    } else {
                        //Toast.makeText(getActivity(), "Permission Denied!", Toast.LENGTH_SHORT).show();
                        showCustomToast(getString(R.string.permission_denied),Toast.LENGTH_LONG);
                    }
                }
                break;

            case STORAGE_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    Log.d("myTag", " Storage:" + writeStorageAccepted);
                    if (writeStorageAccepted) {
                        pickGallery();
                    } else {
                        //Toast.makeText(getActivity(), "Permission Denied!", Toast.LENGTH_SHORT).show();
                        showCustomToast(getString(R.string.permission_denied),Toast.LENGTH_LONG);
                    }
                }
                break;

        }

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case IMAGE_PICK_GALLERY_CODE:
                    CropImage.activity(data.getData())
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .start(getActivity(), ManualForm.this);
                    break;
                case REQUEST_IMAGE_CAPTURE:
                    //Bundle extras = data.getExtras();
                    //imageBitmap = (Bitmap) extras.get("data");
                    CropImage.activity(imageUri)
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .start(getActivity(), ManualForm.this);
                    //Toast.makeText(MainActivity.this,"Error 123:" ,Toast.LENGTH_SHORT).show();

                    //imageBitmap = BitmapFactory.decodeFile(currentImagePath);
                    //imageViewResume.setImageBitmap(imageBitmap);
                    break;
                case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                    CropImage.ActivityResult result = CropImage.getActivityResult(data);
                    resultUri = result.getUri();//get image uri
                    if (profileOrResume == 1) {
                        profileUri = resultUri;
                        binding.imageViewManualProfile.setImageURI(profileUri);
                    } else {
                        resumeUri = resultUri;
                        binding.textViewManualUploadResumeMsg.setVisibility(TextView.VISIBLE);
                        //Toast.makeText(getActivity(), getString(R.string.upload_success), Toast.LENGTH_SHORT).show();
                        showSuccessCustomToast(getString(R.string.upload_success), Toast.LENGTH_SHORT);
                    }
                    //Log.d("Image", "" + profileUri);
                    break;

                case CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE:
                    showCustomToast(getString(R.string.error_crop),Toast.LENGTH_LONG);
                    //Toast.makeText(getActivity(), "Error getting crop image", Toast.LENGTH_SHORT).show();
                    break;

                default:
                    showCustomToast(getString(R.string.error_getting_response),Toast.LENGTH_LONG);
                    //Toast.makeText(getActivity(), "Error getting result", Toast.LENGTH_SHORT).show();
            }
        }
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
