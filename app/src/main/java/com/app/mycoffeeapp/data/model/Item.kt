package com.app.mycoffeeapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Item {

    var createdAt: String? = null

    @PrimaryKey
    var id: Long? = null

    var name: String? = null

    var price: String? = null

    var updatedAt: String? = null
    var imageLocalPath: String? = null
}