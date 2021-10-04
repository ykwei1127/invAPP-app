package com.example.invapp.inventory

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.invapp.R
import com.example.invapp.qrcode.QrcodeActivity
import com.example.invapp.singleton.SingletonClass
import org.json.JSONArray
import org.json.JSONObject

class SingleInventoryPage : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_single_inventory_page, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val textViewSingleUnit = requireView().findViewById<TextView>(R.id.textView_singleUnit)
        val textViewSingleGroup = requireView().findViewById<TextView>(R.id.textView_singleGroup)
        val textViewMSingleName = requireView().findViewById<TextView>(R.id.textView_singleName)
        val textViewSingleCode = requireView().findViewById<TextView>(R.id.textView_singleCode)
        val buttonSave = requireView().findViewById<Button>(R.id.button_save)
        val toggleButton = requireView().findViewById<ToggleButton>(R.id.toggleButton)
        val buttonGoHome = requireView().findViewById<Button>(R.id.button_goHome)
        val buttonBack = requireView().findViewById<Button>(R.id.button_back)
        val textViewSingleInventoryUnit = requireView().findViewById<TextView>(R.id.textView_singleInventoryUnit)
        val progressBarSingleInventory = requireView().findViewById<ProgressBar>(R.id.progressBar_singleInventory)
        val editTextCalculateAmount = requireView().findViewById<EditText>(R.id.editText_calculateAmount)

        val unit : String = SingletonClass.instance.inventoryUnit.toString()
        val group : String = SingletonClass.instance.inventoryGroup.toString()
        val code : String = SingletonClass.instance.inventoryCode.toString()
        val name : String = SingletonClass.instance.inventoryName.toString()

        // 取得加減單一藥品數量的計價單位
        val unitResult = arguments?.getString("unitResult").toString()
        textViewSingleInventoryUnit.text = unitResult

        // 將資料顯示在表格
        textViewSingleUnit.text = unit
        textViewSingleGroup.text = group
        textViewMSingleName.text = name
        textViewSingleCode.text = code

        // 回首頁
        buttonGoHome.setOnClickListener {
            activity?.finish()
        }

        // 上一頁
        buttonBack.setOnClickListener {
            activity?.onBackPressed()
        }

        // 檢查加減按鈕，設置顏色
        toggleButton.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                buttonView.setBackgroundColor(Color.parseColor("#32e370"))
            } else {
                buttonView.setBackgroundColor(Color.parseColor("#ff4444"))
            }
        }

        // 按鈕儲存繼續輸入，送出加減，以及App盤點數量，給server做加減
        buttonSave.setOnClickListener {
            val calculateAmount = editTextCalculateAmount.text.toString()
            if (calculateAmount == "") {
                editTextCalculateAmount.error = "請輸入加減數量"
            } else {
                progressBarSingleInventory.visibility = View.VISIBLE
                val jsonObject = JSONObject()
                jsonObject.put("單位", unit)
                jsonObject.put("組別", group)
                jsonObject.put("代碼", code)
                if (toggleButton.isChecked) {
                    jsonObject.put("App盤點數量", calculateAmount)
                } else {
                    jsonObject.put("App盤點數量", "-$calculateAmount")
                }
                val url = SingletonClass.instance.ip + "/appSingleInventory"
                val queue = Volley.newRequestQueue(activity?.applicationContext)
                val jsonObjectRequest = JsonObjectRequest(
                    Request.Method.POST, url, jsonObject,
                    { response ->
                        println("DEBUG: $response")
                        progressBarSingleInventory.visibility = View.INVISIBLE
                        Toast.makeText(context, "儲存完成", Toast.LENGTH_SHORT).show()
                        activity?.finish()
                        val intent = Intent(activity, InventoryActivity::class.java)
                        startActivity(intent)
                    },
                    {
                        progressBarSingleInventory.visibility = View.INVISIBLE
                        Toast.makeText(context, "儲存失敗", Toast.LENGTH_SHORT).show()
                    })
                queue.add(jsonObjectRequest)
            }
        }
    }

}