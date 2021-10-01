package com.example.invapp.inventory

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import com.example.invapp.R
import com.example.invapp.singleton.SingletonClass

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val textViewSingleUnit = requireView().findViewById<TextView>(R.id.textView_singleUnit)
        val textViewSingleGroup = requireView().findViewById<TextView>(R.id.textView_singleGroup)
        val textViewMSingleName = requireView().findViewById<TextView>(R.id.textView_singleName)
        val textViewSingleCode = requireView().findViewById<TextView>(R.id.textView_singleCode)
        val buttonSave = requireView().findViewById<Button>(R.id.button_save)
        val buttonGoHome = requireView().findViewById<Button>(R.id.button_goHome)
        val buttonBack = requireView().findViewById<Button>(R.id.button_back)

        val unit : String = SingletonClass.instance.inventoryUnit.toString()
        val group : String = SingletonClass.instance.inventoryGroup.toString()
        val code : String = SingletonClass.instance.inventoryCode.toString()
        val name : String = SingletonClass.instance.inventoryName.toString()

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

    }
}