package com.food.foodsensations.database

import androidx.room.Entity
import androidx.room.ColumnInfo
import androidx.room.PrimaryKey


@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey  val resId: String,
    @ColumnInfo(name = "food_items") val foodItems: String
)
