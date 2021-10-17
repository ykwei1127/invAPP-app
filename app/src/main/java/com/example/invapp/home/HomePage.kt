package com.example.invapp.home

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.invapp.R
import com.example.invapp.inventory.InventoryActivity
import com.example.invapp.qrcode.QrcodeActivity
import com.example.invapp.singleton.SingletonClass

class HomePage : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_page, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val buttonQrcode = requireView().findViewById<Button>(R.id.button_qrcode)
        val buttonInventory = requireView().findViewById<Button>(R.id.button_inventory)

        buttonQrcode.setOnClickListener {
            SingletonClass.instance.action = "掃Qrcode盤點"
            startActivity(Intent(context, QrcodeActivity::class.java))
        }
        buttonInventory.setOnClickListener {
            SingletonClass.instance.action = "加減單一藥品數量"
            startActivity(Intent(context, InventoryActivity::class.java))
        }
    }
}