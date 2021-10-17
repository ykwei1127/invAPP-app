package com.example.invapp.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.invapp.R
import com.example.invapp.setting.SettingActivity
import com.example.invapp.singleton.SingletonClass
import org.json.JSONArray
import org.json.JSONObject


class LoginPage : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login_page, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val pref = context?.getSharedPreferences("Setting", Context.MODE_PRIVATE)
        val editTextUsername = requireView().findViewById<EditText>(R.id.editText_username)
        val editTextPassword = requireView().findViewById<EditText>(R.id.editText_password)
        val buttonClearAll = requireView().findViewById<Button>(R.id.button_clearAll)
        val buttonSetting = requireView().findViewById<Button>(R.id.button_setting)
        val buttonLogin = requireView().findViewById<Button>(R.id.button_login)
        val progressBarLogin = requireView().findViewById<ProgressBar>(R.id.progressBar_login)
        val checkBoxLogin = requireView().findViewById<CheckBox>(R.id.checkBox_login)

        val checking = pref?.getString("CHECKING", "0")
        if (checking == "1") {
            val user = pref?.getString("USERNAME", "").toString()
            val password = pref?.getString("PASSWORD", "").toString()
            checkBoxLogin.isChecked = true
            editTextUsername.setText(user)
            editTextPassword.setText(password)
        }

        buttonClearAll.setOnClickListener {
            editTextUsername.setText("")
            editTextPassword.setText("")
        }

        buttonLogin.setOnClickListener {
            when {
                TextUtils.isEmpty(editTextUsername.text) -> {
                    editTextUsername.error = "請輸入職編"
                }
                TextUtils.isEmpty(editTextPassword.text) -> {
                    editTextPassword.error = "請輸入密碼"
                }
                else -> {
                    progressBarLogin.visibility = View.VISIBLE
                    val ip = pref?.getString("IP", "")
                    // save current ip
                    SingletonClass.instance.ip = "http://$ip:8000"
                    val url = SingletonClass.instance.ip+"/appGetUsers"
                    val queue = Volley.newRequestQueue(activity?.applicationContext)
                    val stringRequest = StringRequest(
                        Request.Method.GET, url,
                        { response ->
                            // Log.d("MyTag", "Response: $response")
                            val users = JSONObject(response).toMap()
                            if (users.containsKey(editTextUsername.text.toString())) {
                                //save current user
                                val singletonClass: SingletonClass = SingletonClass.instance
                                singletonClass.currentUser = editTextUsername.text.toString()
                                @Suppress("UNCHECKED_CAST")
                                val user = users[editTextUsername.text.toString()] as? Map<String, String>
                                if (editTextPassword.text.toString() != user?.get("password") ?: String) {
                                    progressBarLogin.visibility = View.INVISIBLE
                                    Toast.makeText(context, "密碼錯誤", Toast.LENGTH_SHORT).show()
                                } else {
                                    val editor = pref?.edit()
                                    if (checkBoxLogin.isChecked) {
                                        editor?.putString("CHECKING", "1")
                                        editor?.putString("USERNAME", editTextUsername.text.toString())
                                        editor?.putString("PASSWORD", editTextPassword.text.toString())
                                        editor?.apply()
                                    } else {
                                        editor?.putString("CHECKING", "0")?.apply()
                                    }
                                    // 確認目前最新一天的盤點，有沒有展開盤點
                                    val url2 = SingletonClass.instance.ip + "/appCheckOpenInventory"
                                    val queue2 = Volley.newRequestQueue(activity?.applicationContext)
                                    val stringRequest2 = StringRequest(
                                        Request.Method.GET, url2,
                                        { response ->
                                            val data = JSONArray(response)
                                            if (data[1] == 0) {
                                                progressBarLogin.visibility = View.INVISIBLE
                                                val controller : NavController = requireView().let { it1 -> Navigation.findNavController(it1) }
                                                controller.navigate(R.id.action_loginPage_to_noEventPage)
                                            } else {
                                                progressBarLogin.visibility = View.INVISIBLE
                                                val bundle = Bundle()
                                                bundle.putString("date", data[0].toString())
                                                bundle.putString("searchResult", response)
                                                val controller : NavController = requireView().let { it1 -> Navigation.findNavController(it1) }
                                                controller.navigate(R.id.action_loginPage_to_homePage, bundle)
                                            }
                                        },
                                        {
                                            // 連線失敗視窗
                                            progressBarLogin.visibility = View.INVISIBLE
                                            val builder : AlertDialog.Builder = AlertDialog.Builder(requireContext())
                                            builder.setTitle("連線失敗，請確認連線")
                                            builder.setPositiveButton("確定") { _, _ -> }
                                            val dialog : AlertDialog = builder.create()
                                            dialog.show()
                                        })
                                    queue2.add(stringRequest2)
                                    //
                                }
                            } else {
                                progressBarLogin.visibility = View.INVISIBLE
                                Toast.makeText(context, "無此帳號", Toast.LENGTH_SHORT).show()
                            }
                        },
                        {
                            progressBarLogin.visibility = View.INVISIBLE
                            val builder : AlertDialog.Builder = AlertDialog.Builder(requireContext())
                            builder.setTitle("連線失敗，請確認連線狀態")
                            builder.setPositiveButton("確定") { _, _ -> }
                            val dialog : AlertDialog = builder.create()
                            dialog.show()
                        })
                    queue.add(stringRequest)
                }
            }
        }

        buttonSetting.setOnClickListener {
            startActivity(Intent(context, SettingActivity::class.java))
        }
    }

    private fun JSONObject.toMap(): Map<String, *> = keys().asSequence().associateWith { it ->
        when (val value = this[it])
        {
            is JSONArray ->
            {
                val map = (0 until value.length()).associate { Pair(it.toString(), value[it]) }
                JSONObject(map).toMap().values.toList()
            }
            is JSONObject -> value.toMap()
            JSONObject.NULL -> null
            else            -> value
        }
    }

}