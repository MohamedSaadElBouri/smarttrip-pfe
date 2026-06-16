package com.example.smarttripvoyager.data.local;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface CircuitDao {
    
    @Query("SELECT * FROM circuits")
    List<CircuitEntity> getAllCircuits();
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<CircuitEntity> circuits);
    
    @Query("DELETE FROM circuits")
    void deleteAll();
}
