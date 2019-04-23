package com.example.android.MyMovieApp.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface MovieDao {

    @Query("SELECT * FROM favorite_movies")
    LiveData<List<Movie>>  getAll();

    @Query("SELECT COUNT(ID) FROM favorite_movies")
    int getCount();
    @Update
    void update(Movie movie);

    @Insert
    void insert(Movie movie);

    @Delete
    void delete(Movie movie);
}
