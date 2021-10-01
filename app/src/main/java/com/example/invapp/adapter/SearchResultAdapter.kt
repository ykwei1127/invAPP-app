package com.example.invapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableRow
import android.widget.TextView
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.invapp.R
import com.example.invapp.singleton.SingletonClass
import org.json.JSONArray
import org.json.JSONObject

class SearchResultAdapter(private val dataset: JSONArray) : RecyclerView.Adapter<SearchResultAdapter.ItemViewHolder>() {
    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewSearchMedCode :TextView = view.findViewById(R.id.textView_searchMedCode)
        val textViewSearchMedName :TextView = view.findViewById(R.id.textView_searchMedName)
        val tableRowSearchResult : TableRow = view.findViewById(R.id.tableRow_searchResult)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context).inflate(R.layout.item_search_result, parent, false)
        return ItemViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val itemString = dataset[position].toString()
        val item = JSONObject(itemString)
        holder.textViewSearchMedCode.text = item.get("代碼").toString()
        holder.textViewSearchMedName.text = item.get("藥名").toString()
        holder.tableRowSearchResult.setOnClickListener {
            SingletonClass.instance.inventoryCode = item.get("代碼").toString()
            SingletonClass.instance.inventoryName = item.get("藥名").toString()
            val controller : NavController = Navigation.findNavController(holder.itemView)
            controller.navigate(R.id.action_searchResultPage_to_selectSearchResultPage)
        }
    }

    override fun getItemCount(): Int {
        return dataset.length()
    }
}