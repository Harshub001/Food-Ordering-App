package com.food.foodsensations.adapter

import android.content.Context
import android.content.Intent

import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.food.foodsensations.R
import com.food.foodsensations.activity.MenuActivity
import com.food.foodsensations.database.RestaurantDatabase
import com.food.foodsensations.database.RestaurantEntity
import com.food.foodsensations.model.Restaurants
import com.squareup.picasso.Picasso

class HomeAdapter(val context: Context, val itemList: ArrayList<Restaurants>) :
    RecyclerView.Adapter<HomeAdapter.HomeViewHolder>() {

    override fun onCreateViewHolder(
        p0: ViewGroup,
        p1: Int
    ): HomeViewHolder {

        val view = LayoutInflater.from(p0.context)
            .inflate(R.layout.recycler_home_single_row, p0, false)
        return HomeViewHolder(view)
    }


    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(p0: HomeViewHolder, p1: Int) {

        val restaurant = itemList[p1]
        p0.txtRestaurantName.text = restaurant.restaurantName
        p0.txtPerPersonRate.text = restaurant.restaurantCostForOne
        p0.txtRating.text = restaurant.restaurantRating
        Picasso.get().load(restaurant.restaurantImage).error(R.drawable.food_delivery_default)
            .into(p0.imgFoodImage)
        p0.llContent.setOnClickListener {

            val intent = Intent(context, MenuActivity::class.java)
            intent.putExtra("restaurant_id", restaurant.restaurantId)
            intent.putExtra("restaurant_name", restaurant.restaurantName)

            context.startActivity(intent)

        }
        val listOfFavourites = GetAllFavAsyncTask(context).execute().get()
        if (listOfFavourites.isNotEmpty() && listOfFavourites.contains(restaurant.restaurantId.toString())) {
            p0.imgFavourite.setImageResource(R.drawable.favourite_2)
        } else {
            p0.imgFavourite.setImageResource(R.drawable.favourite_clip)
        }
        p0.imgFavourite.setOnClickListener {
            val restaurantEntity = RestaurantEntity(
                restaurant.restaurantId,
                restaurant.restaurantName,
                restaurant.restaurantRating,
                restaurant.restaurantCostForOne,
                restaurant.restaurantImage
            )
            if (!DBAsyncTask(context, restaurantEntity, 1).execute().get()) {
                val async =
                    DBAsyncTask(context, restaurantEntity, 2).execute()
                val result = async.get()
                if (result) {
                    p0.imgFavourite.setImageResource(R.drawable.favourite_2)
                }
            } else {
                val async = DBAsyncTask(context, restaurantEntity, 3).execute()
                val result = async.get()

                if (result) {
                    p0.imgFavourite.setImageResource(R.drawable.favourite_clip)
                }
            }
        }
    }

    class HomeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val llContent: LinearLayout = view.findViewById(R.id.llContent)
        val imgFoodImage: ImageView = view.findViewById(R.id.imgFoodImage)
        val txtRestaurantName: TextView = view.findViewById(R.id.txtRestaurantName)
        val txtPerPersonRate: TextView = view.findViewById(R.id.txtPerPersonRate)
        val imgFavourite: ImageView = view.findViewById(R.id.imgFavourite)
        val txtRating: TextView = view.findViewById(R.id.txtRating)
    }

    class DBAsyncTask(val context: Context, val restaurantEntity: RestaurantEntity, val mode: Int) :
        AsyncTask<Void, Void, Boolean>() {
        val db = Room.databaseBuilder(context, RestaurantDatabase::class.java, "res-db").build()
        override fun doInBackground(vararg params: Void?): Boolean {

            when (mode) {
                1 -> {
                    val book: RestaurantEntity? =
                        db.restaurantDao().getRestaurantById(restaurantEntity.resId)
                    db.close()
                    return book != null
                }
                2 -> {

                    db.restaurantDao().insertRestaurant(restaurantEntity)
                    db.close()
                    return true
                }
                3 -> {
                    db.restaurantDao().deleteRestaurant(restaurantEntity)
                    db.close()
                    return true
                }
            }
            return false
        }

    }

    class GetAllFavAsyncTask(
        context: Context
    ) :
        AsyncTask<Void, Void, List<String>>() {

        val db = Room.databaseBuilder(context, RestaurantDatabase::class.java, "res-db").build()
        override fun doInBackground(vararg params: Void?): List<String> {

            val list = db.restaurantDao().getAllRestaurants()
            val listOfIds = arrayListOf<String>()
            for (i in list) {
                listOfIds.add(i.resId)
            }
            return listOfIds
        }
    }
}




