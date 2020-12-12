package com.food.foodsensations.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.food.foodsensations.R
import com.food.foodsensations.model.MenuDetails


class CartAdapter(val context: Context, val orderList:ArrayList<MenuDetails>) :
    RecyclerView.Adapter<CartAdapter.CartViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_cart_single_row, parent, false)
        return CartViewHolder(view)
    }

    override fun getItemCount(): Int {
       return orderList.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val cartObject=orderList[position]
        holder.txtFoodItem.text=cartObject.foodName
        holder.txtFoodItemPrice.text=cartObject.foodCost

    }
    class CartViewHolder(view: View): RecyclerView.ViewHolder(view){
        val txtFoodItem: TextView =view.findViewById(R.id.txtFoodItem)
        val txtFoodItemPrice:TextView=view.findViewById(R.id.txtFoodItemPrice)
    }
}