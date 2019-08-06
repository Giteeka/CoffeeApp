package com.app.mycoffeeapp.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Item {

    var createdAt: String? = null

    @PrimaryKey
    var id: Long? = null

    var name: String? = null

    var price: Float? = null

    var updatedAt: String? = null
}