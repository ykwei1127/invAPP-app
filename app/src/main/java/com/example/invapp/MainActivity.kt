package com.example.invapp

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.navigation.NavController
import androidx.navigation.Navigation

class MainActivity : AppCompatActivity() {

    private lateinit var controller : NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        controller = Navigation.findNavController(this, R.id.fragment)
    }

    override fun onSupportNavigateUp(): Boolean {
        if(controller.currentDestination?.id == R.id.homePage) {
            val builder : AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setTitle("確定離開?")
            builder.setPositiveButton("確定", DialogInterface.OnClickListener { _, _ -> controller.navigateUp() })
            builder.setNegativeButton("取消", DialogInterface.OnClickListener { _, _ ->  })
            val dialog : AlertDialog = builder.create()
            dialog.show()
        } else if (controller.currentDestination?.id == R.id.loginPage) {
            finish()
        } else {
            controller.navigateUp()
        }
        return super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        onSupportNavigateUp()
    }
}