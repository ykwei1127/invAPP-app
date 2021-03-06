package com.example.invapp.qrcode

import android.content.Context
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
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.invapp.R
import com.example.invapp.adapter.ScanResultAdapter
import com.example.invapp.singleton.SingletonClass
import org.json.JSONArray

class ScanResultPage : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_scan_result_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val buttonGoHome = requireView().findViewById<Button>(R.id.button_goHome)
        val buttonBackScan = requireView().findViewById<Button>(R.id.button_backScan)
        val textViewScanResult = requireView().findViewById<TextView>(R.id.textView_scanResult)
        val progressBarScanResult = requireView().findViewById<ProgressBar>(R.id.progressBar_scanResult)

        // 回首頁
        buttonGoHome.setOnClickListener {
            activity?.finish()
        }

        // 返回掃QRCode
        buttonBackScan.setOnClickListener {
            activity?.finish()
            val intent = Intent(activity, QrcodeActivity::class.java)
            startActivity(intent)
        }

        // 列出位置藥品
        var scanResult = arguments?.getString("scanResult").toString()
        // 判斷QRCODE有沒有問題
        if (scanResult.contains("(") and scanResult.contains(")")) {
            val temp = scanResult.replace(")",") ")
            val position = temp.split(" ")
            SingletonClass.instance.qrcodeUnit = position[0].trim()
            SingletonClass.instance.qrcodeGroup = position[1].trim()
            SingletonClass.instance.qrcodeCode = ""
            SingletonClass.instance.qrcodeName = ""

            textViewScanResult.text = scanResult.replace(")",")\n")

            // 取得server qrcode位置的藥品
            scanResult = arguments?.getString("scanResult").toString()
            scanResult = scanResult.replace(")",") ")
            // URL包含中文，轉換%
            val scanResultInURL = java.net.URLEncoder.encode(scanResult, "utf-8").replace("+", "%20")
            val url = SingletonClass.instance.ip + "/appGetQrcodeResults/$scanResultInURL"
            val queue = Volley.newRequestQueue(activity?.applicationContext)
            val stringRequest = StringRequest(
                Request.Method.GET, url,
                { response ->
                    val dataset = JSONArray(response)
                    val recyclerView = view?.findViewById<RecyclerView>(R.id.recyclerView_scanResult)
                    if (dataset.length() != 0){
                        progressBarScanResult.visibility = View.INVISIBLE
                        recyclerView?.adapter = ScanResultAdapter(this, dataset)
                        recyclerView?.setHasFixedSize(true)
                    } else {
                        progressBarScanResult.visibility = View.INVISIBLE
                        Toast.makeText(context, "查無資料，請重新掃描", Toast.LENGTH_LONG).show()
                    }
                },
                {
                    progressBarScanResult.visibility = View.INVISIBLE
                    val recyclerView = view?.findViewById<RecyclerView>(R.id.recyclerView_scanResult)
                    val dataset = JSONArray()
                    recyclerView?.adapter = ScanResultAdapter(this, dataset)
                    recyclerView?.setHasFixedSize(true)
                    progressBarScanResult.visibility = View.INVISIBLE
                    val builder : AlertDialog.Builder = AlertDialog.Builder(requireContext())
                    builder.setTitle("連線失敗，請確認連線狀態")
                    builder.setPositiveButton("確定") { _, _ -> }
                    val dialog : AlertDialog = builder.create()
                    dialog.show()
                })
            queue.add(stringRequest)
        } else {
            progressBarScanResult.visibility = View.INVISIBLE
            textViewScanResult.text = scanResult
            Toast.makeText(context, "QRCODE資料錯誤", Toast.LENGTH_SHORT).show()
        }
    }
}