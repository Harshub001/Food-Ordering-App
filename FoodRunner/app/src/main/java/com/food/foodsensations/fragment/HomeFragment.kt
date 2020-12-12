package com.food.foodsensations.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley

import com.food.foodsensations.R
import com.food.foodsensations.adapter.HomeAdapter
import com.food.foodsensations.model.Restaurants
import com.food.foodsensations.util.ConnectionManager
import org.json.JSONException
import java.util.*
import kotlin.Comparator
import kotlin.collections.HashMap

/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : Fragment() {
    lateinit var recyclerHome: RecyclerView
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: HomeAdapter
    val itemList= arrayOf("Cost(Low to High)","Cost(High to Low)","Rating")

    val restaurantsInfoList = arrayListOf<Restaurants>()
    var ratingComparator= Comparator<Restaurants>
    {restaurant1,restaurant2->
        if(restaurant1.restaurantRating.compareTo(restaurant2.restaurantRating, true)==0){
            restaurant1.restaurantName.compareTo(restaurant2.restaurantName, true)
        }else{
            restaurant1.restaurantRating.compareTo(restaurant2.restaurantRating, true)
        }

    }
    var costComparator= Comparator<Restaurants>
    {restaurant1,restaurant2->
        if(restaurant1.restaurantCostForOne.compareTo(restaurant2.restaurantCostForOne, true)==0){
            restaurant1.restaurantName.compareTo(restaurant2.restaurantName, true)
        }else{
            restaurant1.restaurantCostForOne.compareTo(restaurant2.restaurantCostForOne, true)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        setHasOptionsMenu(true)
        recyclerHome = view.findViewById(R.id.recyclerHome)
        layoutManager = LinearLayoutManager(activity)
        progressLayout = view.findViewById(R.id.progressLayout)
        progressBar = view.findViewById(R.id.progressBar)
        progressLayout.visibility = View.VISIBLE
        val queue = Volley.newRequestQueue(activity as Context)
        val url = "http://13.235.250.119/v2/restaurants/fetch_result/"
        if (ConnectionManager().checkConnectivity(activity as Context)) {
            val jsonObjectRequest =
                object : JsonObjectRequest(Request.Method.GET, url, null, Response.Listener {
                    try {
                        progressLayout.visibility = View.GONE
                        val data = it.getJSONObject("data")
                        val success = data.getBoolean("success")

                        if (success) {
                            val resArray = data.getJSONArray("data")

                            for (i in 0 until resArray.length()) {
                                val restaurantJsonObject = resArray.getJSONObject(i)
                                val restaurantObject = Restaurants(
                                    restaurantJsonObject.getString("id"),
                                    restaurantJsonObject.getString("name"),
                                    restaurantJsonObject.getString("rating"),
                                    restaurantJsonObject.getString("cost_for_one"),
                                    restaurantJsonObject.getString("image_url")
                                )
                               if(activity!=null){
                                restaurantsInfoList.add(restaurantObject)
                                recyclerAdapter =
                                    HomeAdapter(activity as Context, restaurantsInfoList)
                                recyclerHome.adapter = recyclerAdapter
                                recyclerHome.layoutManager = layoutManager}
                            }
                        } else {
                            Toast.makeText(
                                activity as Context,
                                "Some Error Occured!!!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }


                    } catch (e: JSONException) {
                        Toast.makeText(
                            activity as Context,
                            "Some unexpected Error Occured",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }, Response.ErrorListener {

                    Toast.makeText(
                        activity as Context,
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
            val dialog = AlertDialog.Builder(activity as Context)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection is not Found")
            dialog.setPositiveButton("Open Settings") { text, listener ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                activity?.finish()
            }
            dialog.setNegativeButton("Exit") { text, listener ->
                ActivityCompat.finishAffinity(activity as Activity)
            }
            dialog.create()
            dialog.show()
        }

        return view

    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater?.inflate(R.menu.menu_home,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id=item.itemId
        if(id==R.id.action_sort)
        {
            val builder=AlertDialog.Builder(activity)
            builder.setTitle("Sort By?")
            builder.setSingleChoiceItems(itemList,-1){dialog, which ->
                when(which){
                    0->{
                        Collections.sort(restaurantsInfoList,costComparator)

                    }
                    1->{
                        Collections.sort(restaurantsInfoList,costComparator)
                        restaurantsInfoList.reverse()
                    }
                    2->{
                        Collections.sort(restaurantsInfoList,ratingComparator)
                        restaurantsInfoList.reverse()
                    }
                }

            }
            builder.setPositiveButton("Ok",DialogInterface.OnClickListener{dialog, which ->
                recyclerAdapter.notifyDataSetChanged()
                dialog.dismiss()
            })
            builder.setNegativeButton("Cancel",DialogInterface.OnClickListener{dialog, which ->
                dialog.dismiss()
            })
            val alertDialog=builder.create()
            alertDialog.show()
        }
        return super.onOptionsItemSelected(item)
    }


    }



