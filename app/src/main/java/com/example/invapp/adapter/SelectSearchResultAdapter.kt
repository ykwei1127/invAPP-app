package com.example.invapp.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.invapp.R
import com.example.invapp.singleton.SingletonClass
import org.json.JSONArray
import org.json.JSONObject

class SelectSearchResultAdapter(private val dataset: JSONArray) : RecyclerView.Adapter<SelectSearchResultAdapter.ItemViewHolder>() {
    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewSelectMedUnit : TextView = view.findViewById(R.id.textView_selectMedUnit)
        val textViewSelectMedGroup : TextView = view.findViewById(R.id.textView_selectMedGroup)
        val textViewSelectMedAmount : TextView = view.findViewById(R.id.textView_selectMedAmount)
        val tableRowSelectSearchResult : TableRow = view.findViewById(R.id.tableRow_selectSearchResult)
        val tableLayoutSelectSearchResult : TableLayout = view.findViewById(R.id.tableLayout_selectSearchResult)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context).inflate(R.layout.item_select_search_result, parent, false)
        return ItemViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val itemString = dataset[position].toString()
        val item = JSONObject(itemString)
        if (item.get("是否盤點").toString().toInt() == 0) {
            holder.tableLayoutSelectSearchResult.setBackgroundColor(Color.parseColor("#ff4444"))
        } else {
            holder.tableLayoutSelectSearchResult.setBackgroundColor(Color.parseColor("#32e370"))
        }
        holder.textViewSelectMedUnit.text = item.get("單位").toString()
        holder.textViewSelectMedGroup.text = item.get("組別").toString()
        holder.textViewSelectMedAmount.text = item.get("App盤點總數量").toString()

        holder.tableRowSelectSearchResult.setOnClickListener {
            SingletonClass.instance.inventoryUnit = item.get("單位").toString()
            SingletonClass.instance.inventoryGroup= item.get("組別").toString()
            val controller : NavController = Navigation.findNavController(holder.itemView)
            controller.navigate(R.id.action_selectSearchResultPage_to_singleInventoryPage)
        }
    }

    override fun getItemCount(): Int {
        return dataset.length()
    }
}