package com.food.foodsensations.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.food.foodsensations.R
import com.food.foodsensations.util.ConnectionManager
import org.json.JSONException
import org.json.JSONObject

class OtpActivity : AppCompatActivity() {
    lateinit var edtEnterOtp: EditText
    lateinit var edtEnterNewPass: EditText
    lateinit var edtEnterCnfPass: EditText
    lateinit var btnSubmit: Button
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBarOtp: ProgressBar
    var mobileEntered: String? = "100"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)
        showDialog()
        edtEnterOtp = findViewById(R.id.edtEnterOtp)
        edtEnterNewPass = findViewById(R.id.edtEnterNewPass)
        edtEnterCnfPass = findViewById(R.id.edtEnterCnfPass)
        btnSubmit = findViewById(R.id.btnSubmit)
        progressLayout = findViewById(R.id.progressLayout)
        progressBarOtp = findViewById(R.id.progressBarOtp)
        btnSubmit.visibility = View.VISIBLE
        progressLayout.visibility = View.GONE
        progressBarOtp.visibility = View.GONE
        if (intent != null) {
            mobileEntered = intent.getStringExtra("mobile1")
        }

        btnSubmit.setOnClickListener {
            progressBarOtp.visibility = View.VISIBLE
            btnSubmit.visibility = View.GONE
            val otpEntered = edtEnterOtp.text.toString()
            val newPassEntered = edtEnterNewPass.text.toString()
            val cnfPassEntered = edtEnterCnfPass.text.toString()
            if (otpEntered == "" || newPassEntered == "" || cnfPassEntered == "") {
                progressBarOtp.visibility = View.GONE
                btnSubmit.visibility = View.VISIBLE
                Toast.makeText(this@OtpActivity, "All Fields Required1", Toast.LENGTH_SHORT)
                    .show()
            } else {
                if (newPassEntered.length < 6 || cnfPassEntered.length < 6)
                    Toast.makeText(
                        this,
                        "Minimum 6 Characters For Password",
                        Toast.LENGTH_SHORT
                    ).show() else {
                    if (newPassEntered != cnfPassEntered) {
                        progressBarOtp.visibility = View.GONE
                        btnSubmit.visibility = View.VISIBLE
                        Toast.makeText(
                            this@OtpActivity,
                            "Password And Confirm Password Do Not Match",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        val queue = Volley.newRequestQueue(this)
                        val url2 = "http://13.235.250.119/v2/reset_password/fetch_result "
                        val jsonParams2 = JSONObject()
                        jsonParams2.put("mobile_number", mobileEntered)
                        jsonParams2.put("password", newPassEntered)
                        jsonParams2.put("otp", otpEntered)
                        if (ConnectionManager().checkConnectivity(this@OtpActivity)) {
                            val jsonRequest =
                                object : JsonObjectRequest(Method.POST, url2, jsonParams2,
                                    Response.Listener {
                                        val data = it.getJSONObject("data")
                                        val success = data.getBoolean("success")
                                        try {
                                            if (success) {
                                                startActivity(
                                                    Intent(
                                                        this,
                                                        LoginActivity::class.java
                                                    )
                                                )
                                            } else {
                                                progressBarOtp.visibility = View.GONE
                                                btnSubmit.visibility = View.VISIBLE
                                                Toast.makeText(
                                                    this,
                                                    data.getString("errorMessage"),
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        } catch (e: JSONException) {
                                            Toast.makeText(
                                                this,
                                                "Some unexpected error occured",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }, Response.ErrorListener { }) {
                                    override fun getHeaders(): MutableMap<String, String> {
                                        val headers = HashMap<String, String>()
                                        headers["Content-type"] = "application/json"
                                        headers["token"] = "4770be41bb1659"
                                        return headers
                                    }

                                }
                            queue.add(jsonRequest)

                        }
                    }
                }
            }
        }
    }


    fun showDialog() {
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle("Information")
        dialog.setMessage("Please refer to the previous email for the OTP")
        dialog.setPositiveButton("OK") { text, listener ->

        }
        dialog.create()
        dialog.show()
    }
}
