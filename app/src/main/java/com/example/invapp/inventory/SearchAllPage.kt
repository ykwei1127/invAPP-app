package com.example.invapp.inventory

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.invapp.R
import com.example.invapp.adapter.ScanResultAdapter
import com.example.invapp.adapter.SearchResultAdapter
import com.example.invapp.singleton.SingletonClass
import org.json.JSONArray
import org.json.JSONObject

class SearchAllPage : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search_all_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val buttonGoHome = requireView().findViewById<Button>(R.id.button_goHome)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView_searchResult)
        val editTextKeyWord = requireView().findViewById<EditText>(R.id.editText_keyWord)

        // 資料讀取
        val builderReadData : AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builderReadData.setMessage("資料讀取中...")
        val dialogReadData : AlertDialog = builderReadData.create()
        dialogReadData.show()

        // 回首頁
        buttonGoHome.setOnClickListener {
            activity?.finish()
        }

        // 查詢結果表
        SingletonClass.instance.inventoryCode = ""
        SingletonClass.instance.inventoryName = ""
        val url = SingletonClass.instance.ip + "/appGetAllCodeName"
        val queue = Volley.newRequestQueue(activity?.applicationContext)
        val stringRequest = StringRequest(
            Request.Method.GET, url,
            { response ->
                val datasetAll = JSONArray(response)
                recyclerView.adapter = SearchResultAdapter(JSONArray())
                recyclerView.setHasFixedSize(true)

                dialogReadData.dismiss()

                val textWatcher = object : TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {
                    }
                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                    }
                    override fun afterTextChanged(s: Editable?) {
                        if (s.toString() == "") {
                            val dataset = JSONArray()
                            recyclerView.adapter = SearchResultAdapter(dataset)
                            recyclerView.setHasFixedSize(true)
                        } else{
                            val result = mutableSetOf<String>()
                            for (i in 0 until datasetAll.length()) {
                                if (JSONObject(datasetAll[i].toString()).get("藥名").toString().startsWith(s.toString(), true)) {
                                    result.add(datasetAll[i].toString())
                                }
                            }
                            val dataset = JSONArray(result)
                            recyclerView.adapter =  SearchResultAdapter(dataset)
                            recyclerView.setHasFixedSize(true)
                        }
                    }

                }
                editTextKeyWord.addTextChangedListener(textWatcher)
            },
            {
                dialogReadData.dismiss()
                val dataset = JSONArray()
                recyclerView.adapter = SearchResultAdapter(dataset)
                recyclerView.setHasFixedSize(true)
                val builder : AlertDialog.Builder = AlertDialog.Builder(requireContext())
                builder.setTitle("資料讀取失敗，請確認連線狀態")
                builder.setPositiveButton("確定") { _, _ -> }
                val dialog : AlertDialog = builder.create()
                dialog.show()
            })
        queue.add(stringRequest)
    }

}