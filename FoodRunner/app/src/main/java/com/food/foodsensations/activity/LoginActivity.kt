package com.food.foodsensations.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.food.foodsensations.R
import com.food.foodsensations.util.ConnectionManager
import org.json.JSONException
import org.json.JSONObject
import com.food.foodsensations.activity.RegisterActivity as RegisterActivity

class LoginActivity : AppCompatActivity() {
    lateinit var txtSignUp: TextView
    lateinit var txtForgot: TextView
    lateinit var edtMobileNumber: EditText
    lateinit var edtPassword: EditText
    lateinit var btnLogin: Button
    lateinit var progressBarLogin: ProgressBar
    lateinit var sharedPrefs: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        txtForgot = findViewById(R.id.txtForgot)
        edtMobileNumber = findViewById(R.id.edtMobileNumber)
        edtPassword = findViewById(R.id.edtPassword)
        txtSignUp = findViewById(R.id.txtSignUp)
        btnLogin = findViewById(R.id.btnLogin)
        progressBarLogin = findViewById(R.id.progressBarLogin)
        btnLogin.visibility = View.VISIBLE
        progressBarLogin.visibility = View.GONE
        sharedPrefs = getSharedPreferences(getString(R.string.pref_file_name), Context.MODE_PRIVATE)

        val isLoggedIn = sharedPrefs.getBoolean("isLoggedIn", false)
        if (isLoggedIn) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        txtForgot.setOnClickListener {

            progressBarLogin.visibility = View.VISIBLE
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }

        txtSignUp.setOnClickListener {
            progressBarLogin.visibility = View.VISIBLE
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }
        btnLogin.setOnClickListener {
            btnLogin.visibility = View.GONE
            progressBarLogin.visibility = View.VISIBLE
            val queue = Volley.newRequestQueue(this@LoginActivity)
            val loginUrl = "http://13.235.250.119/v2/login/fetch_result/"
            val jsonParams = JSONObject()
            jsonParams.put("mobile_number", edtMobileNumber.text.toString())
            jsonParams.put("password", edtPassword.text.toString())
            if (ConnectionManager().checkConnectivity(this@LoginActivity)) {
                val jsonObjectRequest = object :
                    JsonObjectRequest(Method.POST, loginUrl, jsonParams, Response.Listener {
                        val data = it.getJSONObject("data")
                        val success = data.getBoolean("success")
                        try {

                            if (success) {

                                val userInfoJSONObject = data.getJSONObject("data")
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
                                Toast.makeText(this, "Success!! Welcome", Toast.LENGTH_SHORT)
                                    .show()
                                savePreferences()
                                startActivity(
                                    Intent(
                                        this@LoginActivity,
                                        MainActivity::class.java
                                    )
                                )
                                finish()
                            } else {
                                btnLogin.visibility = View.VISIBLE
                                progressBarLogin.visibility = View.GONE
                                Toast.makeText(
                                    this,
                                    data.getString("errorMessage"),
                                    Toast.LENGTH_SHORT
                                ).show()

                            }

                        } catch (e: JSONException) {
                            btnLogin.visibility = View.VISIBLE
                            progressBarLogin.visibility = View.GONE
                            Toast.makeText(
                                this,
                                "Some unexpected",
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
                queue.add(jsonObjectRequest)
            } else {
                btnLogin.visibility = View.VISIBLE
                progressBarLogin.visibility = View.GONE
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
        }

    }

    fun savePreferences() {
        sharedPrefs.edit().putBoolean("isLoggedIn", true).apply()
    }

    override fun onPause() {
        progressBarLogin.visibility=View.GONE
        super.onPause()
    }
}
