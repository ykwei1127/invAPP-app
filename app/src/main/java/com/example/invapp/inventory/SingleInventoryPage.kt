package com.example.invapp.inventory

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.example.invapp.R
import com.example.invapp.qrcode.QrcodeActivity
import com.example.invapp.singleton.SingletonClass
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

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

        val user : String = SingletonClass.instance.currentUser.toString()
        val action : String = SingletonClass.instance.action.toString()
        val unit : String = SingletonClass.instance.inventoryUnit.toString()
        val group : String = SingletonClass.instance.inventoryGroup.toString()
        val code : String = SingletonClass.instance.inventoryCode.toString()
        val name : String = SingletonClass.instance.inventoryName.toString()

        // 取得加減單一藥品數量的計價單位
        // URL包含中文，轉換%
        val unitInURL = java.net.URLEncoder.encode(unit, "utf-8")
        val groupInURL = java.net.URLEncoder.encode(group, "utf-8")
        val urlUnit = SingletonClass.instance.ip + "/appGetSingleInventoryUnit/$unitInURL/$groupInURL/$code"
        val queueUnit = Volley.newRequestQueue(activity?.applicationContext)
        val stringRequest = StringRequest(
            Request.Method.GET, urlUnit,
            { response ->
                progressBarSingleInventory.visibility = View.INVISIBLE
                // 取得imageURL並顯示圖片
                val jsonArray = JSONArray(response)
                val jsonObjectURL = JSONObject(jsonArray[1].toString())
                val imageURL = jsonObjectURL["URL_URL"].toString()
                val imageView = requireView().findViewById<ImageView>(R.id.imageView_drug)
                Glide.with(this)
                    .load(imageURL)
                    .placeholder(R.drawable.ic_baseline_photo_24)
                    .into(imageView)
                imageView.setOnClickListener {
                    val bundle = Bundle()
                    bundle.putString("imageURL", imageURL)
                    val controller : NavController = requireView().let { it1 -> Navigation.findNavController(it1) }
                    controller.navigate(R.id.action_singleInventoryPage_to_imagePage, bundle)
                }
                // 取得計價單位
                if (jsonArray[0].toString() == "") {
                    Toast.makeText(context, "計價單位取得失敗", Toast.LENGTH_SHORT).show()
                } else {
                    val salesUnit = JSONObject(jsonArray[0].toString())
                    val unitResult = salesUnit.get("計價單位").toString()
                    textViewSingleInventoryUnit.text = unitResult
                }
            },
            {
                val builder : AlertDialog.Builder = AlertDialog.Builder(requireContext())
                builder.setTitle("連線失敗，請確認連線狀態")
                builder.setPositiveButton("確定") { _, _ -> }
                val dialog : AlertDialog = builder.create()
                dialog.show()
            })
        progressBarSingleInventory.visibility = View.VISIBLE
        queueUnit.add(stringRequest)

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
                jsonObject.put("使用者", user)
                // 取得時間
                val sdf = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
                val cal = Calendar.getInstance()
                val time = sdf.format(cal.time)
                jsonObject.put("時間", time)
                jsonObject.put("功能", action)
                jsonObject.put("單位", unit)
                jsonObject.put("組別", group)
                jsonObject.put("代碼", code)
                jsonObject.put("藥名", name)
                if (toggleButton.isChecked) {
                    jsonObject.put("加減數量", calculateAmount)
                    jsonObject.put("App盤點預包數量", "")
                    jsonObject.put("App盤點數量", calculateAmount)
                } else {
                    jsonObject.put("加減數量", "-$calculateAmount")
                    jsonObject.put("App盤點預包數量", "")
                    jsonObject.put("App盤點數量", "-$calculateAmount")
                }
                val url = SingletonClass.instance.ip + "/appSingleInventory"
                val queue = Volley.newRequestQueue(activity?.applicationContext)
                val jsonObjectRequest = JsonObjectRequest(
                    Request.Method.POST, url, jsonObject,
                    { response ->
                        println("DEBUG: $response")
                        val check = response["status"]
                        if (check == "success") {
                            progressBarSingleInventory.visibility = View.INVISIBLE
                            Toast.makeText(context, "儲存完成", Toast.LENGTH_SHORT).show()
                            activity?.finish()
                            val intent = Intent(activity, InventoryActivity::class.java)
                            startActivity(intent)
                        } else if (check == "fail") {
                            progressBarSingleInventory.visibility = View.INVISIBLE
                            val builder : AlertDialog.Builder = AlertDialog.Builder(requireContext())
                            builder.setTitle("盤點已結束，請確認是否已展開盤點")
                            builder.setPositiveButton("確定") { _, _ -> }
                            val dialog : AlertDialog = builder.create()
                            dialog.show()
                        }
                    },
                    {
                        progressBarSingleInventory.visibility = View.INVISIBLE
                        val builder : AlertDialog.Builder = AlertDialog.Builder(requireContext())
                        builder.setTitle("儲存失敗，請確認連線狀態")
                        builder.setPositiveButton("確定") { _, _ -> }
                        val dialog : AlertDialog = builder.create()
                        dialog.show()
                    })
                queue.add(jsonObjectRequest)
            }
        }
    }

}