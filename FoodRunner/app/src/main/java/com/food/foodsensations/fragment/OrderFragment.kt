package com.food.foodsensations.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley

import com.food.foodsensations.R
import com.food.foodsensations.adapter.OrderAdapter
import com.food.foodsensations.model.OrderDetails
import com.food.foodsensations.util.ConnectionManager
import org.json.JSONException

/**
 * A simple [Fragment] subclass.
 */

class OrderFragment : Fragment() {
    lateinit var recyclerOrderHistory: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar
     var orderHistoryList= ArrayList<OrderDetails>()
    lateinit var sharedPrefs: SharedPreferences
    lateinit var recyclerAdapter: OrderAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_order, container, false)
        recyclerOrderHistory = view.findViewById(R.id.recyclerOrderHistory)
        sharedPrefs = activity!!.getSharedPreferences(
            getString(R.string.pref_file_name),
            Context.MODE_PRIVATE
        )
        progressBar = view.findViewById(R.id.progressBar)
        progressLayout = view.findViewById(R.id.progressLayout)
        val userId = sharedPrefs.getString("user_id", "")
        layoutManager = LinearLayoutManager(activity)
        progressLayout.visibility = View.VISIBLE
        progressBar.visibility = View.VISIBLE
        if (ConnectionManager().checkConnectivity(activity as Context)) {
            sendServerRequest(userId.toString())
        }
        return view
    }

    fun sendServerRequest(userId: String) {
        val queue = Volley.newRequestQueue(activity)
        val url = "http://13.235.250.119/v2/orders/fetch_result/${userId}"
        val jsonObjectRequest =
            object : JsonObjectRequest(Method.GET, url, null, Response.Listener {
                try {
                    progressLayout.visibility = View.GONE
                    val data = it.getJSONObject("data")
                    val success = data.getBoolean("success")
                    if (success) {
                        val resArray = data.getJSONArray("data")
                        if (resArray.length() == 0) {

                        } else {
                            for (i in 0 until resArray.length()) {
                                val orderObject = resArray.getJSONObject(i)
                                val foodItems = orderObject.getJSONArray("food_items")
                                val orderDetails = OrderDetails(
                                    orderObject.getString("order_id"),
                                    orderObject.getString("restaurant_name"),
                                    orderObject.getString("order_placed_at"),
                                    foodItems
                                )
                                orderHistoryList.add(orderDetails)
                                if (orderHistoryList.isEmpty()) {

                                } else {

                                }
                                if (activity != null) {
                                    recyclerAdapter =
                                        OrderAdapter(activity as Context, orderHistoryList)
                                    val mLayoutManager = LinearLayoutManager(activity)
                                    recyclerOrderHistory.layoutManager = mLayoutManager
                                    recyclerOrderHistory.adapter = recyclerAdapter
                                } else {
                                    queue.cancelAll(this::class.java.simpleName)
                                }
                            }
                        }
                    }

                } catch (e: JSONException) {
                        e.printStackTrace()
                }
            }, Response.ErrorListener {
                    Toast.makeText(activity as Context,it.message,Toast.LENGTH_SHORT).show()
            }){
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content-type"] = "application/json"
                    headers["token"] = "4770be41bb1659"
                    return headers
                }
            }
        queue.add(jsonObjectRequest)

    }
}


