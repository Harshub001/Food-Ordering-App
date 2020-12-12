package com.food.foodsensations.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.food.foodsensations.R

/**
 * A simple [Fragment] subclass.
 */
class MyprofileFragment : Fragment() {
lateinit var txtProfileName:TextView
    lateinit var txtProfileMobile:TextView
    lateinit var txtProfileEmail:TextView
    lateinit var txtProfileAddress:TextView
    lateinit var sharedPrefs:SharedPreferences
    var s1:String="+91-"
    lateinit var s2:String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view=inflater.inflate(R.layout.fragment_myprofile, container, false)
        txtProfileName=view.findViewById(R.id.txtProfileName)
        txtProfileMobile=view.findViewById(R.id.txtProfileMobile)
        txtProfileEmail=view.findViewById(R.id.txtProfileEmail)
        txtProfileAddress=view.findViewById(R.id.txtProfileAddress)
        txtProfileName.text=sharedPrefs.getString("name","")
        s2= sharedPrefs.getString("mobile_number","").toString()
        s1=s1+s2
        txtProfileMobile.text= s1
        txtProfileEmail.text=sharedPrefs.getString("email","")
        txtProfileAddress.text=sharedPrefs.getString("address","")
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        sharedPrefs = context.getSharedPreferences(getString(R.string.pref_file_name), Context.MODE_PRIVATE)
    }

}
