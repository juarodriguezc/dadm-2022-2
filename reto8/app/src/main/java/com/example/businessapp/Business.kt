package com.example.businessapp


import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "lBusiness")
class Business(
    val name: String,
    val URL: String,
    val telephone: String,
    val email: String,
    val prodServ: String,
    val bType: String,
    @PrimaryKey(autoGenerate = true)
    var idBusiness: Int = 0

) : Serializable
