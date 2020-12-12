package com.food.foodsensations.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View

import android.view.ViewGroup
import android.widget.Button

import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView
import com.food.foodsensations.R

import com.food.foodsensations.model.MenuDetails


class MenuAdapter(
    val context: Context,
    val MenuList: ArrayList<MenuDetails>,
    val listener: onItemClickListener
) :
    RecyclerView.Adapter<MenuAdapter.RestaurantViewHolder>() {

    companion object {
        var isCartEmpty = true
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RestaurantViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_menu_single_row, parent, false)
        return RestaurantViewHolder(view)

    }

    override fun getItemCount(): Int {
        return MenuList.size
    }

    interface onItemClickListener {
        fun onAddItemClick(foodItem: MenuDetails)
        fun onRemoveItemClick(foodItem: MenuDetails)
    }

    override fun onBindViewHolder(
        holder: RestaurantViewHolder,
        position: Int
    ) {
        val menu = MenuList[position]
        holder.foodName.text = menu.foodName
        holder.txtFoodPrice.text = menu.foodCost
        holder.txtSNo.text = (position + 1).toString()
        holder.btnAddtoCart.setOnClickListener {
            holder.btnAddtoCart.visibility = View.GONE
            holder.btnRemovefromCart.visibility = View.VISIBLE
            listener.onAddItemClick(menu)

        }
        holder.btnRemovefromCart.setOnClickListener {
            holder.btnRemovefromCart.visibility = View.GONE
            holder.btnAddtoCart.visibility = View.VISIBLE
            listener.onRemoveItemClick(menu)

        }

    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    class RestaurantViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val foodName: TextView = view.findViewById(R.id.foodName)
        val txtFoodPrice: TextView = view.findViewById(R.id.txtFoodPrice)
        val btnAddtoCart: Button = view.findViewById(R.id.btnAddtoCart)
        val txtSNo: TextView = view.findViewById(R.id.txtSNo)
        val btnRemovefromCart: Button = view.findViewById(R.id.btnRemovefromCart)
    }

}
