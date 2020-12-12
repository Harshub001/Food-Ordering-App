package com.food.foodsensations.fragment

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.recyclerview.widget.LinearLayoutManager

import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room

import com.food.foodsensations.R
import com.food.foodsensations.adapter.FavouriteAdapter
import com.food.foodsensations.database.RestaurantDatabase
import com.food.foodsensations.database.RestaurantEntity

/**
 * A simple [Fragment] subclass.
 */
class FavoriteFragment : Fragment() {
    lateinit var recyclerHome: RecyclerView
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var llContentNoFav:RelativeLayout
    lateinit var recyclerAdapter: FavouriteAdapter
    var dbRestaurantList= listOf<RestaurantEntity>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
val view=inflater.inflate(R.layout.fragment_favourite, container, false)
        recyclerHome = view.findViewById(R.id.recyclerHome)
        layoutManager = LinearLayoutManager(activity)
        progressLayout = view.findViewById(R.id.progressLayout)
        progressBar = view.findViewById(R.id.progressBar)
        progressLayout.visibility = View.VISIBLE
        progressBar.visibility=View.VISIBLE

        dbRestaurantList=RetrieveFavourites(activity as Context).execute().get()
        if(dbRestaurantList.isEmpty()){
        }
        if(activity!=null){
            progressLayout.visibility=View.GONE
            progressBar.visibility=View.GONE
            recyclerAdapter=FavouriteAdapter(activity as Context,dbRestaurantList)
            recyclerHome.adapter=recyclerAdapter
            recyclerHome.layoutManager=layoutManager
        }
        return view
    }
    class RetrieveFavourites(val context: Context): AsyncTask<Void, Void, List<RestaurantEntity>>(){
        override fun doInBackground(vararg params: Void?): List<RestaurantEntity> {
            val db = Room.databaseBuilder(context, RestaurantDatabase::class.java,"res-db").build()
            return db.restaurantDao().getAllRestaurants()
        }

}}
