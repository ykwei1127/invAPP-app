package com.example.invapp.setting

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.invapp.R
import com.example.invapp.login.LoginPage

class SettingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        val editTextIP = findViewById<EditText>(R.id.editText_IP)
        val buttonClear = findViewById<Button>(R.id.button_clear)
        val buttonSetup = findViewById<Button>(R.id.button_setup)

        val pref = getSharedPreferences("Setting", Context.MODE_PRIVATE)
        val ip = pref?.getString("IP", "172.26.2.25")
        if (ip != "") {
            editTextIP.setText(ip)
        }

        buttonClear.setOnClickListener {
            editTextIP.setText("")
        }
        buttonSetup.setOnClickListener {
            if (editTextIP.text.toString() != "") {
                val editor = pref?.edit()
                editor?.putString("IP", editTextIP.text.toString())?.apply()
                Toast.makeText(this, "設定完成", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "請輸入IP位置", Toast.LENGTH_SHORT).show()
            }
        }
    }
}