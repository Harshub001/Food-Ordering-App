package com.food.foodsensations.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.food.foodsensations.R
import com.food.foodsensations.model.OrderDetails
import com.food.foodsensations.model.MenuDetails
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class OrderAdapter(val context: Context, val orderHistoryList: ArrayList<OrderDetails>) :
    RecyclerView.Adapter<OrderAdapter.OrderHistoryViewHolder>() {
    override fun onCreateViewHolder(
        p0: ViewGroup,
        p1: Int
    ): OrderHistoryViewHolder {
        val view = LayoutInflater.from(p0.context)
            .inflate(R.layout.recycler_order_history_single_row, p0, false)
        return OrderHistoryViewHolder(view)
    }

    override fun getItemCount(): Int {
        return orderHistoryList.size
    }

    override fun onBindViewHolder(
        p0: OrderHistoryViewHolder,
        p1: Int
    ) {
        val orderHistoryObj=orderHistoryList[p1]
        p0.txtResName.text=orderHistoryObj.resName
        p0.txtOrderDate.text=formatDate(orderHistoryObj.orderDate)
        setUpRecycler(p0.recyclerResHistory,orderHistoryObj)

    }
    class OrderHistoryViewHolder(view: View): RecyclerView.ViewHolder(view){
        val txtResName:TextView=view.findViewById(R.id.txtResName)
        val txtOrderDate:TextView=view.findViewById(R.id.txtOrderDate)
        val recyclerResHistory:RecyclerView=view.findViewById(R.id.recyclerResHistory)
    }
    fun setUpRecycler(recyclerResHistory:RecyclerView,orderHistoryList:OrderDetails){
        val foodItemsList=ArrayList<MenuDetails>()
        for(i in 0 until orderHistoryList.foodItem.length()){
            val foodJson=orderHistoryList.foodItem.getJSONObject(i)
            foodItemsList.add(
                MenuDetails(
                   foodId =  foodJson.getString("food_item_id"),
                   foodName =  foodJson.getString("name"),
                   foodCost =  foodJson.getString("cost")
                )
            )
        }
        val cartRecyclerAdapter=CartAdapter(context,foodItemsList)
        val layoutManager=LinearLayoutManager(context)
        recyclerResHistory.layoutManager=layoutManager
        recyclerResHistory.adapter=cartRecyclerAdapter
    }
    fun formatDate(dateString:String):String? {
        val inputFormatter=SimpleDateFormat("dd-MM-yy HH:mm:ss", Locale.ENGLISH)
        val date:Date=inputFormatter.parse(dateString) as Date
        val outputFormatter=SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
        return outputFormatter.format(date)
    }
}