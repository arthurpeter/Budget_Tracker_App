package com.android.budgettrackerapp

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "transactions")
data class Transaction (
    @PrimaryKey(autoGenerate = true) val id: Int,
    val label: String,
    var amount: Double,
    val description: String) : Serializable{
}
