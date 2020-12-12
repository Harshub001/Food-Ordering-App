package com.food.foodsensations.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.food.foodsensations.R
import com.food.foodsensations.util.ConnectionManager
import org.json.JSONObject
import java.lang.Exception

class RegisterActivity : AppCompatActivity() {
    lateinit var txtRegisterBack: TextView
    lateinit var edtEnterName: EditText
    lateinit var edtEnterEmail: EditText
    lateinit var edtEnterAddress: EditText
    lateinit var edtEnterMobile: EditText
    lateinit var edtEnterPassword: EditText
    lateinit var edtEnterConfirmPassword: EditText
    lateinit var btnRegister: Button
    lateinit var toolBar: Toolbar
    var emailPattern = Regex("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")
    lateinit var sharedPrefs: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        edtEnterName = findViewById(R.id.edtEnterName)
        edtEnterEmail = findViewById(R.id.edtEnterEmail)
        edtEnterAddress = findViewById(R.id.edtEnterAddress)

        edtEnterMobile = findViewById(R.id.edtEnterMobile)
        btnRegister = findViewById(R.id.btnRegister)
        edtEnterPassword = findViewById(R.id.edtEnterPassword)
        edtEnterConfirmPassword = findViewById(R.id.edtEnterConfirmPassword)
        sharedPrefs = getSharedPreferences(getString(R.string.pref_file_name), Context.MODE_PRIVATE)
        toolBar = findViewById(R.id.toolbar)
        setUpToolbar()
        btnRegister.setOnClickListener {
            btnRegister.visibility = View.GONE
            val name = edtEnterName.text.toString()
            val email = edtEnterEmail.text.toString()
            val phn = edtEnterMobile.text.toString()
            val address = edtEnterAddress.text.toString()
            val pwd = edtEnterPassword.text.toString()
            val confirmPwd = edtEnterConfirmPassword.text.toString()

            if ((name == "") || (email == "") || (phn == "") || (address == "") || (pwd == "") || (confirmPwd == "")) {
                Toast.makeText(this@RegisterActivity, "All Fields Required", Toast.LENGTH_SHORT)
                    .show()
            } else {
                if (phn.length != 10)
                    Toast.makeText(
                        this@RegisterActivity,
                        "Phone Number Should Be Of Exact 10 Digits",
                        Toast.LENGTH_SHORT
                    ).show()
                if (pwd.length < 4 || confirmPwd.length < 4)
                    Toast.makeText(
                        this@RegisterActivity,
                        "Minimum 4 Characters For Password",
                        Toast.LENGTH_SHORT
                    ).show()
                if (pwd != confirmPwd)
                    Toast.makeText(
                        this@RegisterActivity,
                        "Password And Confirm Password Do Not Match",
                        Toast.LENGTH_SHORT
                    ).show()
                if (!email.trim().matches(emailPattern))
                    Toast.makeText(this@RegisterActivity, "Invalid Email", Toast.LENGTH_SHORT)
                        .show()
                else {
                    val queue = Volley.newRequestQueue(this)
                    val url = "http://13.235.250.119/v2/register/fetch_result/"
                    val jsonParams = JSONObject()
                    jsonParams.put("name", name)
                    jsonParams.put("mobile_number", phn)
                    jsonParams.put("password", pwd)
                    jsonParams.put("address", address)
                    jsonParams.put("email", email)
                    if (ConnectionManager().checkConnectivity(this@RegisterActivity)) {

                        val jsonRequest =
                            object : JsonObjectRequest(
                                Method.POST, url, jsonParams,
                                Response.Listener {
                                    try {

                                        val item = it.getJSONObject("data")
                                        val success = item.getBoolean("success")
                                        if (success) {

                                            val userInfoJSONObject = item.getJSONObject("data")
                                            sharedPrefs.edit().putString(
                                                "user_id",
                                                userInfoJSONObject.getString("user_id")
                                            ).apply()
                                            sharedPrefs.edit().putString(
                                                "name",
                                                userInfoJSONObject.getString("name")
                                            ).apply()
                                            sharedPrefs.edit().putString(
                                                "email",
                                                userInfoJSONObject.getString("email")
                                            ).apply()
                                            sharedPrefs.edit().putString(
                                                "mobile_number",
                                                userInfoJSONObject.getString("mobile_number")
                                            ).apply()
                                            sharedPrefs.edit().putString(
                                                "address",
                                                userInfoJSONObject.getString("address")
                                            ).apply()
                                            startActivity(
                                                Intent(
                                                    this@RegisterActivity,
                                                    MainActivity::class.java
                                                )
                                            )
                                        } else {
                                            btnRegister.visibility = View.VISIBLE
                                            Toast.makeText(
                                                this,
                                                ("${item.getString("errorMessage")}"),
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    } catch (e: Exception) {
                                        btnRegister.visibility = View.VISIBLE

                                        Toast.makeText(
                                            this,
                                            "Some Exception Occured $e",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }, Response.ErrorListener {

                                }) {
                                override fun getHeaders(): MutableMap<String, String> {
                                    val headers = HashMap<String, String>()
                                    headers["Content-type"] = "application/json"
                                    headers["token"] = "4770be41bb1659"
                                    return headers
                                }

                            }
                        queue.add(jsonRequest)
                    } else {
                        btnRegister.visibility = View.VISIBLE
                        val dialog = AlertDialog.Builder(this)
                        dialog.setTitle("Error")
                        dialog.setMessage("Internet Connection Not Found. Turn On Internet Connection And Restart App")
                        dialog.setPositiveButton("Close") { text, listner ->
                            ActivityCompat.finishAffinity(this)
                        }
                        dialog.create()
                        dialog.show()
                    }
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        finish()
    }

    fun setUpToolbar() {
        setSupportActionBar(toolBar)
        supportActionBar?.title = "Register Yourself"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

}
