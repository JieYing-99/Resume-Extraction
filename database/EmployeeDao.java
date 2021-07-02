package com.app.smarthire.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface EmployeeDao {
    // allowing the insert of the same word multiple times by passing a
    // conflict resolution strategy
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(EmployeeEntity employee);

    @Query("DELETE FROM employee_table")
    void deleteAll();

    @Query("SELECT * from employee_table")
    LiveData<List<EmployeeEntity>> getAlphabetizedWords();
}
