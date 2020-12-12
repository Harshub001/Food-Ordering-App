package com.food.foodsensations.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.food.foodsensations.R
import com.food.foodsensations.activity.MenuActivity
import com.food.foodsensations.database.RestaurantEntity
import com.squareup.picasso.Picasso

class FavouriteAdapter(val context: Context, val restaurantList: List<RestaurantEntity>):
    RecyclerView.Adapter<FavouriteAdapter.FavouriteViewHolder>()  {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ):FavouriteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_home_single_row, parent, false)
        return FavouriteViewHolder(view)
    }

    override fun getItemCount(): Int {
        return restaurantList.size
    }
    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(
        holder: FavouriteViewHolder,
        position: Int
    ) {
       val resObject=restaurantList[position]
        holder.txtRestaurantName.text = resObject.resName
        holder.txtPerPersonRate.text = resObject.resCostForOne
        holder.txtRating.text = resObject.resRating
        Picasso.get().load(resObject.resImg).error(R.drawable.food_delivery_default)
            .into(holder.imgFoodImage)
       holder.llContent.setOnClickListener {
            val intent = Intent(context, MenuActivity::class.java)
            intent.putExtra("restaurant_id", resObject.resId)
            intent.putExtra("restaurant_name", resObject.resName)
            context.startActivity(intent)
        }
        val listOfFavourites = HomeAdapter.GetAllFavAsyncTask(context).execute().get()
        if (listOfFavourites.isNotEmpty() && listOfFavourites.contains(resObject.resId.toString())) {
            holder.imgFavourite.setImageResource(R.drawable.favourite_2)
        } else {
            holder.imgFavourite.setImageResource(R.drawable.favourite_clip)
        }
        holder.imgFavourite.setOnClickListener{
            val restaurantEntity=RestaurantEntity(
                resObject.resId,
                resObject.resName,
                resObject.resRating,
                resObject.resCostForOne,
                resObject.resImg
            )
            if (!HomeAdapter.DBAsyncTask(context, restaurantEntity, 1).execute().get()) {
                val async =
                    HomeAdapter.DBAsyncTask(context, restaurantEntity, 2).execute()
                val result = async.get()
                if (result) {
                    holder.imgFavourite.setImageResource(R.drawable.favourite_2)
                }
            } else {
                val async = HomeAdapter.DBAsyncTask(context, restaurantEntity, 3).execute()
                val result = async.get()

                if (result) {
                    holder.imgFavourite.setImageResource(R.drawable.favourite_clip)
                }
            }
        }
    }

    class FavouriteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val llContent: LinearLayout = view.findViewById(R.id.llContent)
        val imgFoodImage: ImageView = view.findViewById(R.id.imgFoodImage)
        val txtRestaurantName: TextView = view.findViewById(R.id.txtRestaurantName)
        val txtPerPersonRate: TextView = view.findViewById(R.id.txtPerPersonRate)
        val imgFavourite: ImageView = view.findViewById(R.id.imgFavourite)
        val txtRating: TextView = view.findViewById(R.id.txtRating)
    }

}
