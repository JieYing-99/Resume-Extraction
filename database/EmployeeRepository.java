package com.app.smarthire.database;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class EmployeeRepository {

    private EmployeeDao mEmployeeDao;
    private LiveData<List<EmployeeEntity>> mAllWords;

    // Note that in order to unit test the WordRepository, you have to remove the Application
    // dependency. This adds complexity and much more code, and this sample is not about testing.
    // See the BasicSample in the android-architecture-components repository at
    // https://github.com/googlesamples
    EmployeeRepository(Application application) {
        EmployeeRoomDatabase db = EmployeeRoomDatabase.getDatabase(application);
        mEmployeeDao = db.employeeDao();
        mAllWords = mEmployeeDao.getAlphabetizedWords();
    }

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    LiveData<List<EmployeeEntity>> getAllWords() {
        return mAllWords;
    }

    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
    void insert(EmployeeEntity employee) {
        EmployeeRoomDatabase.databaseWriteExecutor.execute(() -> {
            mEmployeeDao.insert(employee);
        });

    }

}
