package com.food.foodsensations.activity

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.food.foodsensations.R
import com.food.foodsensations.adapter.CartAdapter
import com.food.foodsensations.adapter.MenuAdapter
import com.food.foodsensations.database.OrderDatabase
import com.food.foodsensations.database.OrderEntity
import com.food.foodsensations.model.MenuDetails
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class CartActivity : AppCompatActivity() {
    lateinit var toolbar: Toolbar
    lateinit var progressBar: ProgressBar
    lateinit var recyclerOrderDetail: RecyclerView
    lateinit var progressLayout: RelativeLayout
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: CartAdapter
    lateinit var btnPlaceOrder: Button
    var orderList = ArrayList<MenuDetails>()
    lateinit var txtRestaurantName: TextView
    lateinit var sharedPrefs: SharedPreferences
    var resId: Int? = 0
    var resName: String? = ""
    var userId: String? = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)
        progressBar = findViewById(R.id.progressBar)
        progressLayout = findViewById(R.id.progressLayout)
        txtRestaurantName = findViewById(R.id.txtRestaurantName)
        sharedPrefs = getSharedPreferences(getString(R.string.pref_file_name), Context.MODE_PRIVATE)
        toolbar = findViewById(R.id.toolbar)
        val bundle = intent.getBundleExtra("data")
        resId = bundle?.getInt("resId", 0)
        resName = bundle?.getString("resName", "")
        layoutManager = LinearLayoutManager(this)
        txtRestaurantName.text = resName
        setupToolbar()
        setUpCartList()
        placeOrder()


    }

    fun setupToolbar() {

        setSupportActionBar(toolbar)
        supportActionBar?.title = "My Cart"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    fun setUpCartList() {
        recyclerOrderDetail = findViewById(R.id.recyclerOrderDetail)
        val dbOrderList = RetrieveOrders(applicationContext).execute().get()
        for (element in dbOrderList) {
            orderList.addAll(
                Gson().fromJson(element.foodItems, Array<MenuDetails>::class.java).asList()
            )
        }
        progressLayout.visibility = View.GONE
        recyclerAdapter = CartAdapter(this, orderList)
        recyclerOrderDetail.adapter = recyclerAdapter
        recyclerOrderDetail.layoutManager = layoutManager
    }

    fun placeOrder() {
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder)
        var sum = 0
        for (i in 0 until orderList.size) {
            sum += orderList[i].foodCost.toInt()
        }
        val total = "Place Order(Total: Rs. $sum)"
        btnPlaceOrder.text = total
        btnPlaceOrder.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            sendServerRequest()
        }
    }

    fun sendServerRequest() {
        val queue = Volley.newRequestQueue(this)
        val url = "http://13.235.250.119/v2/place_order/fetch_result/"
        val jsonParams = JSONObject()
        jsonParams.put(
            "user_id",
            this@CartActivity.getSharedPreferences(
                getString(R.string.pref_file_name),
                Context.MODE_PRIVATE
            ).getString("user_id", null) as String
        )

        jsonParams.put("restaurant_id", com.food.foodsensations.activity.MenuActivity.resId?.toString() as String)
        var sum = 0
        for (i in 0 until orderList.size) {
            sum += orderList[i].foodCost.toInt() as Int
        }
        jsonParams.put("total_cost", sum.toString())
        val foodArray = JSONArray()
        for (i in 0 until orderList.size) {
            val foodId1 = JSONObject()
            foodId1.put("food_item_id", orderList[i].foodId)
            foodArray.put(i, foodId1)
        }
        jsonParams.put("food", foodArray)
        val jsonObjectRequest =
            object : JsonObjectRequest(Method.POST, url, jsonParams, Response.Listener {

                try {
                    val data = it.getJSONObject("data")
                    val success = data.getBoolean("success")
                    if (success) {
                        val clearCart =
                            DeleteAllDBAsync(applicationContext).execute().get()
                        MenuAdapter.isCartEmpty = true

                        val dialog = Dialog(
                            this@CartActivity,
                            android.R.style.Theme_Black_NoTitleBar_Fullscreen
                        )
                        dialog.setContentView(R.layout.order_placed_dialog)
                        dialog.show()
                        dialog.setCancelable(false)
                        val btnOk = dialog.findViewById<Button>(R.id.btnOk)
                        btnOk.setOnClickListener {
                            dialog.dismiss()
                            startActivity(Intent(this@CartActivity, MainActivity::class.java))
                            ActivityCompat.finishAffinity(this@CartActivity)
                        }

                    } else {

                        Toast.makeText(
                            this@CartActivity,
                            "Response is $it",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                } catch (e: JSONException) {

                    Toast.makeText(
                        this@CartActivity,
                        "Some Error occurred2",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }, Response.ErrorListener {

                Toast.makeText(this@CartActivity, it.message, Toast.LENGTH_SHORT).show()
            })
            {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content-type"] = "application/json"
                    headers["token"] = "4770be41bb1659"
                    return headers
                }
            }
        queue.add(jsonObjectRequest)
    }

    class RetrieveOrders(val context: Context) : AsyncTask<Void, Void, List<OrderEntity>>() {
        override fun doInBackground(vararg params: Void?): List<OrderEntity> {
            val db = Room.databaseBuilder(context, OrderDatabase::class.java, "order-db").build()
            return db.orderDao().getAllOrders()
        }

    }

    class ClearDBAsync(val context: Context, val resID: String) : AsyncTask<Void, Void, Boolean>() {

        override fun doInBackground(vararg params: Void?): Boolean {
            val db = Room.databaseBuilder(context, OrderDatabase::class.java, "order-db").build()
            db.orderDao().deleteOrders(resID)
            db.close()
            return true
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            val result = ClearDBAsync(applicationContext, resId.toString()).execute().get()
            MenuAdapter.isCartEmpty = true
            if (result) {
                onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        val result = DeleteAllDBAsync(applicationContext).execute().get()
        MenuAdapter.isCartEmpty = true
        if (result) {
            super.onBackPressed()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val clearCart =
            ClearDBAsync(applicationContext, resId.toString()).execute().get()
        MenuAdapter.isCartEmpty = true
        onBackPressed()
        return true
    }

    class DeleteAllDBAsync(val context: Context) : AsyncTask<Void, Void, Boolean>() {

        override fun doInBackground(vararg params: Void?): Boolean {
            val db = Room.databaseBuilder(context, OrderDatabase::class.java, "order-db").build()
            db.orderDao().deleteAllOrders()
            db.close()
            return true
        }

    }
}