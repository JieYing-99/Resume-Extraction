package com.app.smarthire.database;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class EmployeeViewModel extends AndroidViewModel {

    private EmployeeRepository mRepository;

    private LiveData<List<EmployeeEntity>> mAllEmployees;

    public EmployeeViewModel (Application application) {
        super(application);
        mRepository = new EmployeeRepository(application);
        mAllEmployees = mRepository.getAllWords();
    }

    LiveData<List<EmployeeEntity>> getAllWords() { return mAllEmployees; }

    public void insert(EmployeeEntity employeeEntity) { mRepository.insert(employeeEntity); }
}
