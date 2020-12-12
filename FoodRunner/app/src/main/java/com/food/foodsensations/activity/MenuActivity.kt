package com.food.foodsensations.activity


import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.food.foodsensations.R
import com.food.foodsensations.adapter.MenuAdapter
import com.food.foodsensations.database.OrderDatabase
import com.food.foodsensations.database.OrderEntity
import com.food.foodsensations.model.MenuDetails
import com.food.foodsensations.util.ConnectionManager
import com.google.gson.Gson
import org.json.JSONException


class MenuActivity : AppCompatActivity() {
    lateinit var toolbar: Toolbar
    lateinit var progressBar: ProgressBar
    lateinit var recyclerRestaurantDetail: RecyclerView
    lateinit var progressLayout: RelativeLayout
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: MenuAdapter
    lateinit var sharedPrefs: SharedPreferences
    val menuInfoList = ArrayList<MenuDetails>()
    var orderList = ArrayList<MenuDetails>()

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var btnProceedtoCart: Button
        var resId: String? = "100"
        var resName: String? = "name"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        toolbar = findViewById(R.id.toolbar)
        btnProceedtoCart = findViewById(R.id.btnProceedtoCart)
        recyclerRestaurantDetail = findViewById(R.id.recyclerRestaurantDetail)
        layoutManager = LinearLayoutManager(this)
        progressLayout = findViewById(R.id.progressLayout)
        progressBar = findViewById(R.id.progressBar)
        progressBar.visibility = View.VISIBLE
        progressLayout.visibility = View.VISIBLE
        btnProceedtoCart.visibility = View.GONE
        sharedPrefs = getSharedPreferences(getString(R.string.pref_file_name), Context.MODE_PRIVATE)

        if (intent != null) {
            resId = intent.getStringExtra("restaurant_id")
            resName = intent.getStringExtra("restaurant_name")
        } else {
            finish()
            Toast.makeText(
                this,
                "Some unexpected error occured!",
                Toast.LENGTH_SHORT
            ).show()
        }

        setUpToolbar()
        if (ConnectionManager().checkConnectivity(this)) {
            val queue = Volley.newRequestQueue(this)
            val url = "http://13.235.250.119/v2/restaurants/fetch_result/${resId}"
            val jsonObjectRequest = object : JsonObjectRequest(
                Method.GET,
                url,
                null,
                Response.Listener {
                    try {
                        val data = it.getJSONObject("data")
                        val success = data.getBoolean("success")
                        if (success) {
                            progressBar.visibility = View.GONE
                            progressLayout.visibility = View.GONE
                            val resMenuArray = data.getJSONArray("data")
                            for (i in 0 until resMenuArray.length()) {
                                val restaurantJsonObject = resMenuArray.getJSONObject(i)
                                val foodItem = MenuDetails(
                                    foodId = restaurantJsonObject.getString("id"),
                                    foodName = restaurantJsonObject.getString("name"),
                                    foodCost = restaurantJsonObject.getString("cost_for_one")
                                )
                                menuInfoList.add(foodItem)

                                recyclerAdapter =
                                    MenuAdapter(
                                        this@MenuActivity,
                                        menuInfoList,
                                        object : MenuAdapter.onItemClickListener {
                                            override fun onAddItemClick(foodItem: MenuDetails) {

                                                orderList.add(foodItem)
                                                if (orderList.size > 0) {
                                                    btnProceedtoCart.visibility = View.VISIBLE
                                                    MenuAdapter.isCartEmpty =
                                                        false
                                                }
                                            }

                                            override fun onRemoveItemClick(foodItem: MenuDetails) {
                                                orderList.remove(foodItem)
                                                if (orderList.isEmpty()) {
                                                    btnProceedtoCart.visibility = View.GONE
                                                    MenuAdapter.isCartEmpty = true
                                                }
                                            }
                                        }
                                    )
                                recyclerRestaurantDetail.adapter = recyclerAdapter
                                recyclerRestaurantDetail.layoutManager = layoutManager
                            }
                        } else {
                            Toast.makeText(
                                this,
                                "Some Error Occured!!!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: JSONException) {
                        Toast.makeText(
                            this,
                            "Some unexpected Error Occured",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                Response.ErrorListener {
                    Toast.makeText(
                        this,
                        "Volley Error Occured",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content-type"] = "application/json"
                    headers["token"] = "4770be41bb1659"
                    return headers
                }
            }
            queue.add(jsonObjectRequest)
        } else {
            val dialog = AlertDialog.Builder(this)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection is not Found")
            dialog.setPositiveButton("Open Settings") { text, listener ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)

            }
            dialog.setNegativeButton("Exit") { text, listener ->
                ActivityCompat.finishAffinity(this)
            }
            dialog.create()
            dialog.show()
        }
        btnProceedtoCart.setOnClickListener {
            val async1 = CountDBAsync(applicationContext).execute().get()
            if (async1 == 0) {
                val gson = Gson()
                val foodItems = gson.toJson(orderList)
                val async =
                    InsertDBAsyncTask(applicationContext, resId.toString(), foodItems).execute()
                val success = async.get()
                if (success) {
                    val data = Bundle()
                    data.putString("resId", resId)
                    data.putString("resName", resName)
                    val intent = Intent(this, CartActivity::class.java)
                    intent.putExtra("data", data)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Some unexpected error occured", Toast.LENGTH_SHORT).show()
                }
            }
            else{
                Toast.makeText(this,"database not empty count is ${async1}",Toast.LENGTH_SHORT).show()
                DeleteAllDBAsync(applicationContext).execute()
            }
        }
    }

    fun setUpToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = resName
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onBackPressed() {
        if (orderList.size > 0) {
            val dialog = AlertDialog.Builder(this)
            dialog.setTitle("Confirmation")
            dialog.setMessage("Going back will reset cart items.Do you still want to proceed?")
            dialog.setPositiveButton("YES") { text, listener ->
                DeleteAllDBAsync(applicationContext).execute()
                super.onBackPressed()
            }
            dialog.setNegativeButton("NO") { text, listener ->

            }

            dialog.create()
            dialog.show()
        } else if (orderList.isEmpty()) {
            super.onBackPressed()
        }


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            if (orderList.size > 0) {
                val dialog = AlertDialog.Builder(this)
                dialog.setTitle("Confirmation")
                dialog.setMessage("Going back will reset cart items.Do you still want to proceed?")
                dialog.setPositiveButton("YES") { text, listener ->
                    DeleteAllDBAsync(applicationContext).execute()
                    MenuAdapter.isCartEmpty = true
                    startActivity(Intent(this, MainActivity::class.java))
                }
                dialog.setNegativeButton("NO") { text, listener ->

                }

                dialog.create()
                dialog.show()
            } else if (orderList.isEmpty()) {
                startActivity(Intent(this, MainActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)


    }


    class InsertDBAsyncTask(
        val context: Context,
        val resId: String,
        val foodItems: String

    ) :
        AsyncTask<Void, Void, Boolean>() {
        val db = Room.databaseBuilder(context, OrderDatabase::class.java, "order-db").build()
        override fun doInBackground(vararg params: Void?): Boolean {
            db.orderDao().insertOrder(OrderEntity(resId, foodItems))
            db.close()
            return true
        }
    }

    class CountDBAsync(val context: Context) : AsyncTask<Void, Void, Int>() {

        override fun doInBackground(vararg params: Void?): Int {
            val db = Room.databaseBuilder(context, OrderDatabase::class.java, "order-db").build()
            return db.orderDao().checkEmpty()
        }

    }
    class DeleteAllDBAsync(val context: Context) : AsyncTask<Void, Void, Boolean>() {

        override fun doInBackground(vararg params: Void?): Boolean{
            val db = Room.databaseBuilder(context, OrderDatabase::class.java, "order-db").build()
            db.orderDao().deleteAllOrders()
            db.close()
            return true
        }

    }

}
