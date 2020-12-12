package com.food.foodsensations.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.food.foodsensations.R
import com.food.foodsensations.util.ConnectionManager
import org.json.JSONException
import org.json.JSONObject

class ForgotPasswordActivity :AppCompatActivity() {
    lateinit var edtMobileForgot: EditText
    lateinit var edtForgotEmail: EditText
    lateinit var btnNext: Button
    lateinit var progressBarForgot: ProgressBar
    lateinit var mobile: String
    lateinit var email: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot)
        edtMobileForgot = findViewById(R.id.edtMobileForgot)
        edtForgotEmail = findViewById(R.id.edtForgotEmail)
        btnNext = findViewById(R.id.btnNext)
        progressBarForgot = findViewById(R.id.progressBarForgot)
        progressBarForgot.visibility = View.GONE
        mobile = edtMobileForgot.text.toString()
        email = edtForgotEmail.text.toString()


        btnNext.setOnClickListener {
            progressBarForgot.visibility = View.VISIBLE
            val queue = Volley.newRequestQueue(this@ForgotPasswordActivity)
            val url = "http://13.235.250.119/v2/forgot_password/fetch_result"
            val jsonParams = JSONObject()
            jsonParams.put("mobile_number", edtMobileForgot.text.toString())
            jsonParams.put("email", edtForgotEmail.text.toString())
            val intent = Intent(this, OtpActivity::class.java)
            intent.putExtra("mobile1", edtMobileForgot.text.toString())
            if (ConnectionManager().checkConnectivity(this)) {
                val jsonObjectRequest =
                    object : JsonObjectRequest(Method.POST, url, jsonParams, Response.Listener {
                        val data = it.getJSONObject("data")
                        val success = data.getBoolean("success")
                        try {
                            if (success) {
                                val firsttry = data.getBoolean("first_try")
                                if (!firsttry) {
                                    startActivity(intent)
                                } else {
                                    startActivity(intent)
                                }
                            } else {
                                btnNext.visibility = View.VISIBLE
                                progressBarForgot.visibility = View.GONE
                                Toast.makeText(
                                    this,
                                    data.getString("errorMessage"),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } catch (e: JSONException) {
                            btnNext.visibility = View.VISIBLE
                            progressBarForgot.visibility = View.GONE
                            Toast.makeText(
                                this,
                                "Some unexpected error occured",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    }, Response.ErrorListener {
                        btnNext.visibility = View.VISIBLE
                        progressBarForgot.visibility = View.GONE
                        Toast.makeText(
                            this,
                            "Some unexpected error occured",
                            Toast.LENGTH_SHORT
                        ).show()
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
                val dialog = android.app.AlertDialog.Builder(this)
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
}



