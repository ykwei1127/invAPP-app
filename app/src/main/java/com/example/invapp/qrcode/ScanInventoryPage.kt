package com.example.invapp.qrcode

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.invapp.R
import com.example.invapp.singleton.SingletonClass
import org.json.JSONArray
import org.json.JSONObject


class ScanInventoryPage : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_scan_inventory_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val textViewMedUnit = requireView().findViewById<TextView>(R.id.textView_qrcodeUnit)
        val textViewMedGroup = requireView().findViewById<TextView>(R.id.textView_qrcodeGroup)
        val textViewMedName = requireView().findViewById<TextView>(R.id.textView_qrcodeName)
        val textViewMedCode = requireView().findViewById<TextView>(R.id.textView_qrcodeCode)
        val textViewPrePackedUnit = requireView().findViewById<TextView>(R.id.textView_prePackedUnit)
        val textViewSalesUnit = requireView().findViewById<TextView>(R.id.textView_salesUnit)
        val editTextInputPrePackedNumber = requireView().findViewById<EditText>(R.id.editText_inputPrePackedNumber)
        val editTextInputNumber = requireView().findViewById<EditText>(R.id.editText_inputNumber)
        val textViewAutoCount = requireView().findViewById<TextView>(R.id.textView_autoCount)
        val buttonSave = requireView().findViewById<Button>(R.id.button_save)
        val progressBarQrcode = requireView().findViewById<ProgressBar>(R.id.progressBar_qrcode)
        val buttonGoHome = requireView().findViewById<Button>(R.id.button_goHome)
        val buttonBack = requireView().findViewById<Button>(R.id.button_back)

        val unit : String = SingletonClass.instance.qrcodeUnit.toString()
        val group : String = SingletonClass.instance.qrcodeGroup.toString()
        val code : String = SingletonClass.instance.qrcodeCode.toString()
        val name : String = SingletonClass.instance.qrcodeName.toString()

        // 將資料顯示在表格
        textViewMedUnit.text = unit
        textViewMedGroup.text = group
        textViewMedCode.text = code
        textViewMedName.text = name

        // 回首頁
        buttonGoHome.setOnClickListener {
            activity?.finish()
        }

        // 上一頁
        buttonBack.setOnClickListener {
            activity?.onBackPressed()
        }

        // 取得盤點的預包單位、計價單位、數量，自動顯示計算結果
        val url = SingletonClass.instance.ip + "/appGetData/$unit/$group/$code"
        val queue = Volley.newRequestQueue(activity?.applicationContext)
        val stringRequest = StringRequest(
            Request.Method.GET, url,
            { response ->
                val jsonArray = JSONArray(response)
                val jsonObject = JSONObject(jsonArray[0].toString())
                if (jsonObject["預包單位"].toString() == "無") {
                    textViewPrePackedUnit.visibility = View.INVISIBLE
                    editTextInputPrePackedNumber.setText("0")
                    editTextInputPrePackedNumber.isEnabled = false
                    textViewAutoCount.text = "0"
                }
                textViewPrePackedUnit.text = jsonObject["預包單位"].toString()
                textViewSalesUnit.text = jsonObject["計價單位"].toString()

                val prePackedNumber = jsonObject["預包數量"].toString().toInt()

                val textWatcher = object : TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {}

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {}

                    override fun afterTextChanged(s: Editable?) {
                        if (s != null && s.toString() != "") {
                            if (editTextInputPrePackedNumber.text.hashCode() == s.hashCode()) {
                                val value = s.toString()
                                editTextInputPrePackedNumber.removeTextChangedListener(this)
                                if (editTextInputNumber.text.toString() != "") {
                                    val n1 = value.toInt()
                                    val n2 = editTextInputNumber.text.toString().toInt()
                                    textViewAutoCount.text = (n1 * prePackedNumber + n2).toString()
                                } else {
                                    val n1 = value.toInt()
                                    textViewAutoCount.text = (n1 * prePackedNumber).toString()
                                }
                                editTextInputPrePackedNumber.addTextChangedListener(this)
                            } else if (editTextInputNumber.text.hashCode() == s.hashCode()) {
                                val value = s.toString()
                                editTextInputNumber.removeTextChangedListener(this)
                                if (editTextInputPrePackedNumber.text.toString() != "") {
                                    val n1 = editTextInputPrePackedNumber.text.toString().toInt()
                                    val n2 = value.toInt()
                                    textViewAutoCount.text = (n1 * prePackedNumber + n2).toString()
                                } else {
                                    textViewAutoCount.text = value
                                }
                                editTextInputNumber.addTextChangedListener(this)
                            }
                        }  else {
                            if (editTextInputPrePackedNumber.text.hashCode() == s.hashCode() && editTextInputNumber.text.toString() != "") {
                                textViewAutoCount.text = editTextInputNumber.text.toString()
                            } else if (editTextInputNumber.text.hashCode() == s.hashCode() && editTextInputPrePackedNumber.text.toString() != "") {
                                val n1 = editTextInputPrePackedNumber.text.toString().toInt()
                                textViewAutoCount.text = (n1 * prePackedNumber).toString()
                            } else {
                                textViewAutoCount.text = "即時小計"
                            }
                        }
                    }

                }
                editTextInputPrePackedNumber.addTextChangedListener(textWatcher)
                editTextInputNumber.addTextChangedListener(textWatcher)
            },
            { error ->
                println("DEBUG: $error")
                val builder : AlertDialog.Builder = AlertDialog.Builder(requireContext())
                builder.setTitle("連線失敗，請確認連線狀態")
                builder.setPositiveButton("確定") { _, _ -> }
                val dialog : AlertDialog = builder.create()
                dialog.show()
            })
        queue.add(stringRequest)

        // 儲存繼續輸入
        buttonSave.setOnClickListener {
            val result = textViewAutoCount.text.toString()
            if (result == "即時小計") {
                Toast.makeText(context, "請輸入數量", Toast.LENGTH_SHORT).show()
            } else {
                progressBarQrcode.visibility = View.VISIBLE
                val jsonObject = JSONObject()
                jsonObject.put("單位", unit)
                jsonObject.put("組別", group)
                jsonObject.put("代碼", code)
                when {
                    editTextInputPrePackedNumber.text.toString() == "" -> {
                        jsonObject.put("App盤點預包數量", 0)
                        jsonObject.put("App盤點數量", editTextInputNumber.text.toString().toInt())
                    }
                    editTextInputNumber.text.toString() == "" -> {
                        jsonObject.put("App盤點預包數量", editTextInputPrePackedNumber.text.toString().toInt())
                        jsonObject.put("App盤點數量", 0)
                    }
                    else -> {
                        jsonObject.put("App盤點預包數量", editTextInputPrePackedNumber.text.toString().toInt())
                        jsonObject.put("App盤點數量", editTextInputNumber.text.toString().toInt())
                    }
                }
                println("DEBUG: $jsonObject")
                val url2 = SingletonClass.instance.ip + "/appInventory"
                val que = Volley.newRequestQueue(activity?.applicationContext)
                val jsonObjectRequest = JsonObjectRequest(
                    Request.Method.POST, url2, jsonObject,
                    {  response ->
                        println("DEBUG: $response")
                        progressBarQrcode.visibility = View.INVISIBLE
                        Toast.makeText(context, "儲存完成", Toast.LENGTH_SHORT).show()
                        val navController : NavController = Navigation.findNavController(requireActivity(), R.id.fragment_qrcode)
                        navController.navigateUp()
                    },
                    {
                        progressBarQrcode.visibility = View.INVISIBLE
                        val builder : AlertDialog.Builder = AlertDialog.Builder(requireContext())
                        builder.setTitle("連線失敗，請確認連線狀態")
                        builder.setPositiveButton("確定") { _, _ -> }
                        val dialog : AlertDialog = builder.create()
                        dialog.show()
                    }
                )
                que.add(jsonObjectRequest)
            }
        }

    }

}