package com.example.businessapp

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface BusinessDao {

    //Create
    @Insert
    fun insertAll(vararg business: Business)

    //Read
    @Query("SELECT * FROM lBusiness")
    fun getAll(): LiveData<List<Business>>

    @Query("SELECT * FROM lBusiness WHERE idBusiness = :idBusiness")
    fun getBusiness(idBusiness: Int): LiveData<Business>

    //Filter by

    //Update
    @Update
    fun updateBusiness(business: Business)

    //Delete
    @Delete
    fun deleteBusiness(business: Business)
}