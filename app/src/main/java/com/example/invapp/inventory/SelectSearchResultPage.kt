package com.example.invapp.inventory

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.invapp.R
import com.example.invapp.adapter.SelectSearchResultAdapter
import com.example.invapp.singleton.SingletonClass
import org.json.JSONArray

class SelectSearchResultPage : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_select_search_result_page, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val buttonGoHome = requireView().findViewById<Button>(R.id.button_goHome)
        val buttonGoSearch = requireView().findViewById<Button>(R.id.button_goSearch)
        val textViewSelectResult = requireView().findViewById<TextView>(R.id.textView_selectResult)
        val recyclerViewSelectSearchResult = requireView().findViewById<RecyclerView>(R.id.recyclerView_selectSearchResult)
        val progressBarSelectSearchResult = requireView().findViewById<ProgressBar>(R.id.progressBar_selectSearchResult)

        // 選擇的藥品名稱
        textViewSelectResult.text = SingletonClass.instance.inventoryName

        // 選擇藥品位置及盤點量之列表
        SingletonClass.instance.inventoryUnit = ""
        SingletonClass.instance.inventoryGroup = ""
        val code = SingletonClass.instance.inventoryCode
        val url = SingletonClass.instance.ip + "/appGetSelectResult/$code"
        val queue = Volley.newRequestQueue(activity?.applicationContext)
        val stringRequest = StringRequest(
            Request.Method.GET, url,
            { response ->
                if (JSONArray(response) == JSONArray()) {
                    progressBarSelectSearchResult.visibility = View.INVISIBLE
                    Toast.makeText(context, "列出藥品位置失敗", Toast.LENGTH_SHORT).show()
                } else {
                    progressBarSelectSearchResult.visibility = View.INVISIBLE
                    val dataset = JSONArray(response)
                    recyclerViewSelectSearchResult.adapter = SelectSearchResultAdapter(dataset)
                    recyclerViewSelectSearchResult.setHasFixedSize(true)
                }
            },
            {
                progressBarSelectSearchResult.visibility = View.INVISIBLE
                Toast.makeText(context, "連線失敗", Toast.LENGTH_SHORT).show()
            })
        queue.add(stringRequest)

        // 回首頁
        buttonGoHome.setOnClickListener {
            activity?.finish()
        }

        // 返回查尋頁面
        buttonGoSearch.setOnClickListener {
            activity?.finish()
            val intent = Intent(activity, InventoryActivity::class.java)
            startActivity(intent)
        }
    }

}