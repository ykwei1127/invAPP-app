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
import com.example.invapp.qrcode.ScanResultPage
import com.example.invapp.singleton.SingletonClass
import org.json.JSONArray
import org.json.JSONObject

class ScanResultAdapter(private val context: ScanResultPage, private val dataset: JSONArray) : RecyclerView.Adapter<ScanResultAdapter.ItemViewHolder>() {
    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewMedCode : TextView = view.findViewById(R.id.textView_medCode)
        val textViewMedName : TextView = view.findViewById(R.id.textView_medName)
        val textViewMedTotalAmount : TextView = view.findViewById(R.id.textView_medTotalAmount)
        val tableLayout : TableLayout = view.findViewById(R.id.tableLayout)
        val tableRow : TableRow = view.findViewById(R.id.tableRow)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context).inflate(R.layout.item, parent, false)
        return ItemViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val itemString = dataset[position].toString()
        val item = JSONObject(itemString)
        if (item.get("是否盤點").toString().toInt() == 0) {
            holder.tableLayout.setBackgroundColor(Color.parseColor("#ff4444"))
        } else {
            holder.tableLayout.setBackgroundColor(Color.parseColor("#32e370"))
        }
        holder.textViewMedCode.text = item.get("代碼").toString()
        holder.textViewMedName.text = item.get("藥名").toString()
        if (item.get("App盤點總數量").toString() == "null") {
            holder.textViewMedTotalAmount.text = ""
        } else {
            holder.textViewMedTotalAmount.text = item.get("App盤點總數量").toString()
        }

        holder.tableRow.setOnClickListener {
            SingletonClass.instance.qrcodeCode = item.get("代碼").toString()
            SingletonClass.instance.qrcodeName= item.get("藥名").toString()
            val controller : NavController = Navigation.findNavController(holder.itemView)
            controller.navigate(R.id.action_scanResultPage_to_scanInventoryPage)
        }

    }

    override fun getItemCount(): Int {
        return dataset.length()
    }
}