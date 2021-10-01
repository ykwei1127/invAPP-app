package com.example.invapp.inventory

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.example.invapp.R
import com.example.invapp.adapter.SearchResultAdapter
import com.example.invapp.singleton.SingletonClass
import org.json.JSONArray

class SearchResultPage : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search_result_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val buttonGoHome = requireView().findViewById<Button>(R.id.button_goHome)
        val buttonGoSearch = requireView().findViewById<Button>(R.id.button_goSearch)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView_searchResult)

        // 查詢結果表
        SingletonClass.instance.inventoryCode = ""
        SingletonClass.instance.inventoryName = ""
        val searchResult = arguments?.getString("searchResult").toString()
        val dataset = JSONArray(searchResult)
        recyclerView.adapter = SearchResultAdapter(dataset)
        recyclerView.setHasFixedSize(true)

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