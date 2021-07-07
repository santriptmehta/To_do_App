package com.santriptmehta.to_do_app

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable


@Entity(tableName = ToDoDatabase.TABLE_NAME)
class ToDo: Serializable {


    @PrimaryKey(autoGenerate = true)
    var todoID : Long? = null
    var name: String? = null
}

// serializable and parcelable
//reflection