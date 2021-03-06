package com.example.invapp.qrcode

import android.content.Intent
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
import com.bumptech.glide.Glide
import com.example.invapp.R
import com.example.invapp.inventory.InventoryActivity
import com.example.invapp.singleton.SingletonClass
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*


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

        val user : String = SingletonClass.instance.currentUser.toString()
        val action : String = SingletonClass.instance.action.toString()
        val unit : String = SingletonClass.instance.qrcodeUnit.toString()
        val group : String = SingletonClass.instance.qrcodeGroup.toString()
        val code : String = SingletonClass.instance.qrcodeCode.toString()
        val name : String = SingletonClass.instance.qrcodeName.toString()

        // ????????????????????????
        textViewMedUnit.text = unit
        textViewMedGroup.text = group
        textViewMedCode.text = code
        textViewMedName.text = name

        // ?????????
        buttonGoHome.setOnClickListener {
            activity?.finish()
        }

        // ?????????
        buttonBack.setOnClickListener {
            activity?.onBackPressed()
        }

        // ??????????????????????????????????????????????????????????????????????????????
        // URL?????????????????????%
        val unitInURL = java.net.URLEncoder.encode(unit, "utf-8")
        val groupInURL = java.net.URLEncoder.encode(group, "utf-8")
        val url = SingletonClass.instance.ip + "/appGetData/$unitInURL/$groupInURL/$code"
        val queue = Volley.newRequestQueue(activity?.applicationContext)
        val stringRequest = StringRequest(
            Request.Method.GET, url,
            { response ->
                progressBarQrcode.visibility = View.INVISIBLE
                val jsonArray = JSONArray(response)
                // ?????????????????????????????????URL->????????????
                val jsonObjectURL = JSONObject(jsonArray[1].toString())
                val imageURL = jsonObjectURL["URL_URL"].toString()
                val imageView = requireView().findViewById<ImageView>(R.id.imageView_qrcodeDrug)
                Glide.with(this)
                    .load(imageURL)
                    .placeholder(R.drawable.ic_baseline_photo_24)
                    .into(imageView)
                imageView.setOnClickListener {
                    val bundle = Bundle()
                    bundle.putString("imageURL", imageURL)
                    val controller : NavController = requireView().let { it1 -> Navigation.findNavController(it1) }
                    controller.navigate(R.id.action_scanInventoryPage_to_imagePage2, bundle)
                }
                // ???????????????????????????????????????
                val jsonObject = JSONObject(jsonArray[0].toString())
                if (jsonObject["????????????"].toString() == "???") {
                    textViewPrePackedUnit.visibility = View.INVISIBLE
                    editTextInputPrePackedNumber.setText("0")
                    editTextInputPrePackedNumber.isEnabled = false
                    textViewAutoCount.text = "0"
                    textViewPrePackedUnit.text = jsonObject["????????????"].toString()
                } else {
                    val prePackedUnitString = jsonObject["????????????"].toString() + '(' + jsonObject["????????????"].toString() + ')'
                    textViewPrePackedUnit.text = prePackedUnitString
                }

                textViewSalesUnit.text = jsonObject["????????????"].toString()

                val prePackedNumber = jsonObject["????????????"].toString().toInt()

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
                                textViewAutoCount.text = "????????????"
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
                builder.setTitle("????????????????????????????????????")
                builder.setPositiveButton("??????") { _, _ -> }
                val dialog : AlertDialog = builder.create()
                dialog.show()
            })
        progressBarQrcode.visibility = View.VISIBLE
        queue.add(stringRequest)

        // ??????????????????
        buttonSave.setOnClickListener {
            val result = textViewAutoCount.text.toString()
            if (result == "????????????") {
                Toast.makeText(context, "???????????????", Toast.LENGTH_SHORT).show()
            } else {
                progressBarQrcode.visibility = View.VISIBLE
                val jsonObject = JSONObject()
                jsonObject.put("?????????", user)
                // ????????????
                val sdf = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
                val cal = Calendar.getInstance()
                val time = sdf.format(cal.time)
                jsonObject.put("??????", time)
                jsonObject.put("??????", action)
                jsonObject.put("??????", unit)
                jsonObject.put("??????", group)
                jsonObject.put("??????", code)
                jsonObject.put("??????", name)
                jsonObject.put("????????????", "")
                when {
                    editTextInputPrePackedNumber.text.toString() == "" -> {
                        jsonObject.put("App??????????????????", 0)
                        jsonObject.put("App????????????", editTextInputNumber.text.toString().toInt())
                    }
                    editTextInputNumber.text.toString() == "" -> {
                        jsonObject.put("App??????????????????", editTextInputPrePackedNumber.text.toString().toInt())
                        jsonObject.put("App????????????", 0)
                    }
                    else -> {
                        jsonObject.put("App??????????????????", editTextInputPrePackedNumber.text.toString().toInt())
                        jsonObject.put("App????????????", editTextInputNumber.text.toString().toInt())
                    }
                }
                println("DEBUG: $jsonObject")
                val url2 = SingletonClass.instance.ip + "/appInventory"
                val que = Volley.newRequestQueue(activity?.applicationContext)
                val jsonObjectRequest = JsonObjectRequest(
                    Request.Method.POST, url2, jsonObject,
                    {  response ->
                        println("DEBUG: $response")
                        val check = response["status"]
                        if (check == "success") {
                            progressBarQrcode.visibility = View.INVISIBLE
                            Toast.makeText(context, "????????????", Toast.LENGTH_SHORT).show()
                            val navController : NavController = Navigation.findNavController(requireActivity(), R.id.fragment_qrcode)
                            navController.navigateUp()
                        } else if (check == "fail") {
                            progressBarQrcode.visibility = View.INVISIBLE
                            val builder : AlertDialog.Builder = AlertDialog.Builder(requireContext())
                            builder.setTitle("????????????????????????????????????????????????")
                            builder.setPositiveButton("??????") { _, _ -> }
                            val dialog : AlertDialog = builder.create()
                            dialog.show()
                        }
                    },
                    {
                        progressBarQrcode.visibility = View.INVISIBLE
                        val builder : AlertDialog.Builder = AlertDialog.Builder(requireContext())
                        builder.setTitle("????????????????????????????????????")
                        builder.setPositiveButton("??????") { _, _ -> }
                        val dialog : AlertDialog = builder.create()
                        dialog.show()
                    }
                )
                que.add(jsonObjectRequest)
            }
        }

    }

}